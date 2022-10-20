package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.data.*;
import scc.utils.Hash;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Path("/auction")
public class AuctionResource {

    private static final DataProxy dataProxy = DataProxy.getInstance();

    public AuctionResource() {}

    /**
     * Creates a new auction
     * @param auction the auction details
     * @param owner_pwd the password of the auction owner
     * @return the created auction
     * @throws BadRequestException if the auction already exists
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Auction create(Auction auction, @HeaderParam("Authorization") String owner_pwd)
            throws BadRequestException, WebApplicationException
    {
        validateAuctionFields(auction, true);

        login(auction.getOwnerNickname(), owner_pwd);

        if(dataProxy.getAuction(auction.auctionID()).isPresent())
            throw new BadRequestException("Auction already exists");

        return dataProxy.createAuction(auction).orElse(null);

    }

    /**
     /**
     * Updates the details an auction
     * @param auction new auction details
     * @param auctionId id of the auction
     * @param owner_pwd the password of the auction owner
     * @return the updated auction
     * @throws WebApplicationException if the new auction details are not valid
     * @throws NotFoundException if the auction could not be found
     * @throws NotAuthorizedException the auction is closed
     */
    @PUT
    @Path("/{auction_id}")
    public Auction update(Auction auction, @PathParam("auction_id") String auctionId,
                       @HeaderParam("Authorization") String owner_pwd) throws WebApplicationException, NotFoundException, NotAuthorizedException
    {
        validateAuctionFields(auction, false);

        Auction prevAuctionDetails = validateAuction(auctionId, owner_pwd);

        Auction newDetails = prevAuctionDetails.patch(auction).orElse(null);

        if (newDetails == null)
            throw new NotAuthorizedException("This auction is closed.");

        return dataProxy.updateAuctionInfo(auctionId, newDetails).orElse(null);
    }

    /**
     * @param auctionId the id of the auction
     * @return a list of bids associated with an auction
     * @throws NotFoundException when the auction could not be found
     */
    @GET
    @Path("/{auction_id}/bid")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bid> listBids(@PathParam("auction_id") String auctionId) throws NotFoundException
    {
        validateAuction(auctionId, null);

        return dataProxy.getAuctionBids(auctionId);
    }

    /**
     * Posts a bid under this auction
     * @param bid the bid details
     * @param auctionId the auction to bid in
     * @param bidder_pwd the bidder password
     * @throws NotFoundException if the auction or the bidder do not exist
     * @throws NotAuthorizedException if the bidder password is incorrect
     */
    @POST
    @Path("/{auction_id}/bid")
    @Consumes(MediaType.APPLICATION_JSON)
    public void doBid(Bid bid, @PathParam("auction_id") String auctionId,
                      @HeaderParam("Authorization") String bidder_pwd) throws NotFoundException, NotAuthorizedException
    {
        validateAuction(auctionId, null);

        login(bid.getBidder(), bidder_pwd);

        dataProxy.executeBid(auctionId, bid);
    }

    /**
     * @param auctionId the auction id
     * @return the Q&A list under an auction
     * @throws NotFoundException if the auction could not be found
     */
    @GET
    @Path("/{auction_id}/question")
    public List<Question> listQuestions(@PathParam("auction_id") String auctionId) throws NotFoundException
    {
        validateAuction(auctionId, null);

        return dataProxy.getAuctionQuestions(auctionId);
    }

    /**
     * Submits a question under an auction
     * @param question the question details
     * @param auctionId the id of the auction the reply will be under
     * @param pwd the password of the questioner
     * @return the created question details
     * @throws NotFoundException if the auction could not be found
     * @throws NotAuthorizedException if the questioner could not be found
     */
    @POST
    @Path("/{auction_id}/question")
    @Consumes(MediaType.APPLICATION_JSON)
    public Question submitQuestion(Question question, @PathParam("auction_id") String auctionId,
                               @HeaderParam("Authorization") String pwd) throws NotFoundException, NotAuthorizedException
    {   
        //validate the auction (não sei se aqui se usaria o metodo login)
        validateAuction(auctionId, null);
        login(question.getQuestioner(), pwd);
        Question nQuestion = question.copy();
        nQuestion.setAnswer(null);

        return dataProxy.createQuestion(auctionId, nQuestion).orElse(null);
    }

    /**
     * Submits a reply to a question under an auction
     * @param question the details of the reply
     * @param auctionId the id of the auction the reply is under
     * @param pwd the password of the auction owner
     * @return the updated question
     * @throws NotAuthorizedException if the auction owner details are incorrect
     * @throws NotFoundException if the auction does not exist or there is no such question to reply to
     */
    @PUT
    @Path("/{auction_id}/question")
    @Consumes(MediaType.APPLICATION_JSON)
    public Question submitReply(Question question, @PathParam("auction_id") String auctionId,
                            @HeaderParam("Authorization") String pwd) throws NotAuthorizedException, NotFoundException
    {
        validateAuction(auctionId, pwd);
        
        Question realQuestion = dataProxy.getQuestion(question.getQuestionID()).orElse(null);
        
        if (realQuestion == null)
            throw new NotFoundException("Question not found");

        return dataProxy.updateQuestion(auctionId, question.getQuestionID(), realQuestion).orElse(null);
    }

    /**
     * Shows the auctions of a user
     * @param nickname the nickname of the user
     * @return the user auctions
     */
    @GET
    @Path("/user/{nickname}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Auction> showUserAuctions(@PathParam("nickname") String nickname)
    {   
        //validate the user
        //Return the auctions with the nickname if exists
        throw new NotSupportedException();
    }

    /**
     * Shows the auctions that are about to close (<5 minutes)
     * @return the auctions closing in 5 minutes
     */
    @GET
    @Path("/closing")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Auction> showAuctionsAboutToClose()
    {
        
        throw new NotSupportedException();
    }

    /**
     * Logs in and returns a user
     * @param nickname the user nickname
     * @param pwd the user password
     * @return the user with the given nickname
     * @throws NotFoundException if the user could not be found
     * @throws NotAuthorizedException if the password is incorrect
     */
    static User login(String nickname, String pwd) throws NotFoundException, NotAuthorizedException
    {
        Optional<User> o = dataProxy.getUser(nickname);

        if (o.isEmpty())
            throw new NotFoundException("User does not exist.");

        User u = o.get();
        if (! u.getPwd().equals(Hash.of(pwd)))
            throw new NotAuthorizedException("Password incorrect.");

        return u;
    }

    /**
     * Validates if an auction with the given auction id exists and has valid data
     * also can optionally check if the auction owner pwd is right
     * @param auctionOwnerPwd the password of the auction owner, or null to disable this check.
     * @return the auction in the data layer
     */
    static Auction validateAuction(String auctionID, String auctionOwnerPwd) throws NotFoundException, NotAuthorizedException
    {
        Auction auctionDetails = dataProxy.getAuction(auctionID).orElse(null);

        if (auctionDetails == null)
            throw new NotFoundException("Auction not found.");

        User auctionOwner = dataProxy.getUser(auctionDetails.getOwnerNickname()).orElse(null);

        if (auctionOwner == null)
        {
            dataProxy.deleteAuction(auctionID);
            throw new NotFoundException("User associated with the auction does not exist.");
        }

        if (Objects.nonNull(auctionOwnerPwd) && ! auctionOwner.getPwd().equals(Hash.of(auctionOwnerPwd)))
            throw new NotAuthorizedException("Password Incorrect.");

        return auctionDetails;
    }

    /**
     * Validates auction fields
     * @param auction the auction to validate
     * @param requireNonNull if auction fields should be
     * @throws WebApplicationException if a field contains an invalid value
     */
    static void validateAuctionFields(Auction auction, boolean requireNonNull) throws WebApplicationException
    {
        String error_message = null;

        if (Objects.isNull(auction))
            error_message = "Auction can not be null";
        else {
            if (Objects.requireNonNullElse(auction.getTitle(), requireNonNull ? "" : "a").isBlank())
                error_message = "Title can not be blank.";
            if (Objects.requireNonNullElse(auction.getOwnerNickname(), requireNonNull ? "" : "a").isBlank())
                error_message = "Owner name can not be blank.";
            if(auction.getEndTime() < System.currentTimeMillis())
                error_message = "Introduce a valid end time.";
            if(auction.getMinPrice() <= 0)
                error_message = "Introduce a valid minimum price.";
            if (Objects.nonNull(auction.getOwnerNickname()))
                if (dataProxy.getUser(auction.getOwnerNickname()).isEmpty())
                    error_message = "Auction owner does not exist.";
        }

        if (Objects.nonNull(error_message))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode(), error_message).build());
    }
}

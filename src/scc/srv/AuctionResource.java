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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Auction create(Auction auction, @HeaderParam("Authorization") String owner_pwd)
    {
        validateAuctionFields(auction, true);

        login(auction.ownerNickname(), owner_pwd);

        if(dataProxy.getAuction(auction.auctionID()).isPresent())
            throw new BadRequestException("Auction already exists");

        return dataProxy.createAuction(auction).orElse(null);

    }

    @PUT
    @Path("/{auction_id}")
    public Auction update(Auction auction, @PathParam("auction_id") String auctionId,
                       @HeaderParam("Authorization") String owner_pwd)
    {
        validateAuctionFields(auction, false);

        Auction prevAuctionDetails = validateAuction(auctionId, owner_pwd);

        Auction newDetails = prevAuctionDetails.patch(auction).orElse(null);

        if (newDetails == null)
            throw new NotAuthorizedException("Could not update auction.");

        return dataProxy.updateAuctionInfo(auctionId, newDetails).orElse(null);
    }

    @GET
    @Path("/{auction_id}/bid")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bid> listBids(@PathParam("auction_id") String auctionId)
    {
        validateAuction(auctionId, null);

        return dataProxy.getAuctionBids(auctionId);
    }

    @POST
    @Path("/{auction_id}/bid")
    @Consumes(MediaType.APPLICATION_JSON)
    public void doBid(Bid bid, @PathParam("auction_id") String auctionId,
                      @HeaderParam("Authorization") String bidder_pwd)
    {
        validateAuction(auctionId, null);

        login(bid.bidderNickname(), bidder_pwd);

        dataProxy.executeBid(auctionId, bid);
    }

    @GET
    @Path("/{auction_id}/question")
    public List<Question> listQuestions(@PathParam("auction_id") String auctionId)
    {
        validateAuction(auctionId, null);

        return dataProxy.getAuctionQuestions(auctionId);
    }

    @POST
    @Path("/{auction_id}/question")
    @Consumes(MediaType.APPLICATION_JSON)
    public Question submitQuestion(Question question, @PathParam("auction_id") String auctionId,
                               @HeaderParam("Authorization") String pwd)
    {
        // TODO validate auction, and if the pwd provided corresponds
        //  to the auction owner, see method validateAuction

        return dataProxy.createQuestion(auctionId, question).orElse(null);
    }

    @PUT
    @Path("/{auction_id}/question")
    @Consumes(MediaType.APPLICATION_JSON)
    public Question submitReply(Question question, @PathParam("auction_id") String auctionId,
                            @HeaderParam("Authorization") String pwd)
    {
        // TODO validate auction, and if the pwd provided corresponds
        //  to the auction owner, see method validateAuction

        Question prevQuestion = null;

        Question newQuestion = question; // prevQuestion.patch(question);

        return dataProxy.updateQuestion(auctionId, question.getQuestionID(), newQuestion).orElse(null);
    }

    @GET
    @Path("/user/{nickname}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Auction> showUserAuctions(@PathParam("nickname") String nickname)
    {
        throw new NotSupportedException();
    }

    @GET
    @Path("/closing")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Auction> showAuctionsAboutToClose()
    {
        throw new NotSupportedException();
    }

    static User login(String nickname, String pwd) {
        Optional<User> o = dataProxy.getUser(nickname);

        if (o.isEmpty())
            throw new NotFoundException("User does not exist.");

        User u = o.get();
        if (! u.pwd().equals(Hash.of(pwd)))
            throw new NotAuthorizedException("Password incorrect.");

        return u;
    }

    /**
     * Validates if an auction with the given auction id exists and has valid data
     * also can optionally check if the auction owner pwd is right
     * @param auctionOwnerPwd the password of the auction owner, or null to disable this check.
     * @return the auction in the data layer
     */
    static Auction validateAuction(String auctionID, String auctionOwnerPwd)
    {
        Auction auctionDetails = dataProxy.getAuction(auctionID).orElse(null);

        if (auctionDetails == null)
            throw new NotFoundException("Auction not found.");

        User auctionOwner = dataProxy.getUser(auctionDetails.ownerNickname()).orElse(null);

        if (auctionOwner == null)
        {
            dataProxy.deleteAuction(auctionID);
            throw new NotFoundException("User associated with the auction does not exist.");
        }

        if (Objects.nonNull(auctionOwnerPwd) && ! auctionOwner.pwd().equals(Hash.of(auctionOwnerPwd)))
            throw new NotAuthorizedException("Password Incorrect.");

        return auctionDetails;
    }

    /**
     * Validates auction fields
     * @throws WebApplicationException if a field contains an invalid value
     */
    static void validateAuctionFields(Auction auction, boolean requireNonNull) throws WebApplicationException
    {
        String error_message = null;

        if (Objects.isNull(auction))
            error_message = "Auction can not be null";
        else {
            if (Objects.requireNonNullElse(auction.title(), requireNonNull ? "" : "a").isBlank())
                error_message = "Title can not be blank.";
            if (Objects.requireNonNullElse(auction.ownerNickname(), requireNonNull ? "" : "a").isBlank())
                error_message = "Owner name can not be blank.";
            if(auction.endTime() < System.currentTimeMillis())
                error_message = "Introduce a valid end time.";
            if(auction.minPrice() <= 0)
                error_message = "Introduce a valid minimum price.";
            if (Objects.nonNull(auction.ownerNickname()))
                if (dataProxy.getUser(auction.ownerNickname()).isEmpty())
                    error_message = "Auction owner does not exist.";
        }

        if (Objects.nonNull(error_message))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode(), error_message).build());
    }
}

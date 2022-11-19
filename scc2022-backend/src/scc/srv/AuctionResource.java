package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import scc.data.*;
import scc.session.Session;

import java.util.*;

@Path("/auction")
public class AuctionResource {

    private static final DataProxy dataProxy = DataProxy.getInstance();

    public AuctionResource() {}

    /**
     * Creates a new auction
     * @param auction the auction details
     * @param auctionOwnerSessionCookie the cookie that authenticates the owner of the auction being created
     * @return the created auction
     * @throws WebApplicationException if an error occurred while creating the auction
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Auction create(Auction auction, @CookieParam(Session.COOKIE_NAME) Cookie auctionOwnerSessionCookie)
            throws WebApplicationException
    {
        validateAuctionFields(auction, true);

        UserResource.validateUserSession(auctionOwnerSessionCookie, auction.getOwnerNickname());

        if(dataProxy.getAuction(auction.getAuctionID()).isPresent())
            throw new BadRequestException("Auction already exists");

        auction.setAuctionID(UUID.randomUUID().toString()); // generate random auction id

        return dataProxy.createAuction(auction).orElse(null);

    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Auction> search(@QueryParam("query") String queryString)
    {
        if (Objects.isNull(queryString))
            throw new BadRequestException("Query string should not be empty");

        return dataProxy.searchAuctions(queryString);
    }

    /**
     /**
     * Updates the details an auction
     * @param auction new auction details
     * @param auctionId id of the auction
     * @param auctionOwnerSessionCookie the cookie that authenticates the auction owner
     * @return the updated auction
     * @throws WebApplicationException if there was an error updating the auction
     */
    @PATCH
    @Path("/{auction_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Auction update(Auction auction, @PathParam("auction_id") String auctionId,
                       @CookieParam(Session.COOKIE_NAME) Cookie auctionOwnerSessionCookie)
            throws WebApplicationException
    {
        validateAuctionFields(auction, false);

        Auction prevAuctionDetails = validateAuction(auctionId, auctionOwnerSessionCookie);

        Auction newDetails = prevAuctionDetails.patch(auction).orElse(null);

        if (newDetails == null)
            throw new NotAuthorizedException("This auction is closed.");

        UserResource.validateUserSession(auctionOwnerSessionCookie, newDetails.getOwnerNickname());

        return dataProxy.updateAuctionInfo(auctionId, newDetails).orElse(null);
    }

    @GET
    @Path("/{auction_id}/bid/highest")
    @Produces(MediaType.APPLICATION_JSON)
    public Bid getHighestBid(@PathParam("auction_id") String auctionId) throws NotFoundException
    {
        validateAuction(auctionId, null);

        return dataProxy.getHighestBid(auctionId).orElse(null);
    }
    /**
     * @param auctionId the id of the auction
     * @return a list of bids associated with an auction
     * @throws WebApplicationException if there was an error getting the auction or the bids
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
     * Posts a bid under an auction
     * @param bid the bid details
     * @param auctionId the auction to bid in
     * @param bidderSessionCookie the cookie that authenticates the bidder
     * @throws WebApplicationException if there was an error validating the auction or authenticating the bidder
     */
    @POST
    @Path("/{auction_id}/bid")
    @Consumes(MediaType.APPLICATION_JSON)
    public void doBid(Bid bid, @PathParam("auction_id") String auctionId,
                      @CookieParam(Session.COOKIE_NAME) Cookie bidderSessionCookie)
            throws WebApplicationException
    {
        validateAuction(auctionId, null);

        UserResource.validateUserSession(bidderSessionCookie, bid.getBidder());

        dataProxy.executeBid(UUID.randomUUID().toString(), auctionId, bid);
    }

    /**
     * @param auctionId the auction id
     * @return the Q&A list under an auction
     * @throws WebApplicationException if there was an error fetching the auction or it's questions
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
     * @param askerSessionCookie the session cookie that authenticates the questioner
     * @return the created question details
     * @throws WebApplicationException if there was an error submiting the question
     */
    @POST
    @Path("/{auction_id}/question")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Question submitQuestion(Question question, @PathParam("auction_id") String auctionId,
                                   @CookieParam(Session.COOKIE_NAME) Cookie askerSessionCookie)
            throws NotFoundException, NotAuthorizedException
    {
        Auction a = validateAuction(auctionId, null);
        if (a.getOwnerNickname().equals(question.getQuestioner()))
            throw new NotAuthorizedException("The auction owner can not submit questions about their own auction.");

        UserResource.validateUserSession(askerSessionCookie, question.getQuestioner());

        question.setAnswer(null);
        question.setQuestionID(UUID.randomUUID().toString());

        return dataProxy.createQuestion(auctionId, question).orElse(null);
    }

    /**
     * Submits a reply to a question under an auction
     * @param question the details of the reply
     * @param auctionId the id of the auction the reply is under
     * @param ownerSessionCookie the session cookie that authenticates the auction owner
     * @return the updated question
     * @throws WebApplicationException if there was an error replying to the question
     */
    @PUT
    @Path("/{auction_id}/question")
    @Consumes(MediaType.APPLICATION_JSON)
    public Question submitReply(Question question, @PathParam("auction_id") String auctionId,
                                @CookieParam(Session.COOKIE_NAME) Cookie ownerSessionCookie)
            throws NotAuthorizedException, NotFoundException
    {
        validateAuction(auctionId, ownerSessionCookie);
        
        Question realQuestion = dataProxy.getQuestion(question.getQuestionID()).orElse(null);
        
        if (realQuestion == null)
            throw new NotFoundException("Question not found");

        return dataProxy.updateQuestion(auctionId, question.getQuestionID(), realQuestion.patch(question)).orElse(null);
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
        return dataProxy.getAuctionsClosingInXMinutes(10);
    }

    @GET
    @Path("/recentlyUpdated")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Auction> showAuctionsRecentlyUpdated()
    {
        return dataProxy.getRecentlyUpdatedAuctions();
    }

    /**
     * Validates if an auction with the given auction id exists and has valid data
     * also can optionally check if a cookie authenticates the auction owner
     * @param auctionOwnerSessionCookie the session cookie of the auction owner, or null to disable this check.
     * @return the auction in the data layer
     */
    static Auction validateAuction(String auctionID, Cookie auctionOwnerSessionCookie)
            throws NotFoundException, NotAuthorizedException
    {
        Auction auctionDetails = dataProxy.getAuction(auctionID)
                .orElseThrow(() -> new NotFoundException("Auction not found."));


        if (auctionDetails.getOwnerNickname() != null && auctionOwnerSessionCookie != null)
            UserResource.validateUserSession(auctionOwnerSessionCookie, auctionDetails.getOwnerNickname());

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
            throw new BadRequestException(error_message);
    }
}

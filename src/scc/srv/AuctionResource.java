package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.database.Auction;
import scc.database.AuctionDAO;
import scc.database.CosmosDBLayer;
import scc.database.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Path("/auction")
public class AuctionResource {

    private static final String TOKEN_QUERY_PARAM = "auth_token";

    private static final CosmosDBLayer dbLayer = CosmosDBLayer.getInstance();

    public AuctionResource() {}

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Auction create(Auction auction, @QueryParam(TOKEN_QUERY_PARAM) String authToken)
    {
        validateAuctionFields(auction, true);

        if(dbLayer.getAuctionByTitle(auction.getTitle()).stream().findAny().isPresent())
            throw new BadRequestException("Auction already exists");

        AuctionDAO newAuction = new AuctionDAO(auction);

        return dbLayer.putAuction(newAuction).getItem().toAuction();
    }

    @PUT
    @Path("/{auction_id}")
    public void update(Auction auction, @PathParam("auction_id") String id,
                       @QueryParam(TOKEN_QUERY_PARAM) String authToken)
    {
        throw new NotSupportedException();
    }

    @GET
    @Path("/{auction_id}/bid")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bid> listBids(@PathParam("auction_id") String id)
    {
        throw new NotSupportedException();
    }

    @POST
    @Path("/{auction_id}/bid")
    @Consumes(MediaType.APPLICATION_JSON)
    public void doBid(Bid bid, @PathParam("auction_id") String id,
                      @QueryParam(TOKEN_QUERY_PARAM) String authToken)
    {
        throw new NotSupportedException();
    }

    @GET
    @Path("/{auction_id}/question")
    public List<Question> listQuestions(@PathParam("auction_id") String id)
    {
        throw new NotSupportedException();
    }

    @POST
    @Path("/{auction_id}/question")
    @Consumes(MediaType.APPLICATION_JSON)
    public void submitQuestionOrReply(Question question, @PathParam("auction_id") String id,
                                      @QueryParam(TOKEN_QUERY_PARAM) String authToken,
                                      @QueryParam("answer") Boolean isAnswer)
    {
        throw new NotSupportedException();
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

    //record Auction(String title, String desc, String photoId, String owner_nickname, long endTime, float minPrice, String aucStatus, List<Bid> bids, List<Question> questions) {}

    record Bid(String bidder_nickname, Double amount) {}

    record Question(String author_nickname, String text, String answer) {}

    /**
     * Validates user fields
     * @throws WebApplicationException if a field contains an invalid
     */
    private void validateAuctionFields(Auction auction, boolean requireNonNull) throws WebApplicationException
    {
        String error_message = null;

        if (Objects.isNull(auction))
            error_message = "Auction can not be null";
        else {
            if (Objects.requireNonNullElse(auction.getTitle(), requireNonNull ? "" : "a").isBlank())
                error_message = "Title can not be blank.";
            if (Objects.requireNonNullElse(auction.getOwner_nickname(), requireNonNull ? "" : "a").isBlank())
                error_message = "Owner name can not be blank.";
            if(auction.getEndTime() <= 0)
                error_message = "Introduce a valid end time.";
            if(auction.getMinPrice() <= 0)
                error_message = "Introduce a valid minimum price.";
            if (Objects.requireNonNullElse(auction.getAucStatus(), requireNonNull ? "" : "a").isBlank())
                error_message = "Auction status can not be blank.";
        }

        if (Objects.nonNull(error_message))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode(), error_message).build());
    }
}

package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import scc.database.CosmosDBLayer;

import java.util.List;
import java.util.Map;

@Path("/auction")
public class AuctionResource {

    private static final String TOKEN_QUERY_PARAM = "auth_token";

    private static final CosmosDBLayer dbLayer = CosmosDBLayer.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String create(Auction auction, @QueryParam(TOKEN_QUERY_PARAM) String authToken)
    {
        throw new NotSupportedException();
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

    record Auction(String owner_nickname, List<Bid> bids, List<Question> questions) {}

    record Bid(String bidder_nickname, Double amount) {}

    record Question(String author_nickname, String text, String answer) {}
}

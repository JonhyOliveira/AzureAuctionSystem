package scc.data.layers;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import scc.data.AuctionDAO;
import scc.data.BidDAO;
import scc.data.QuestionDAO;

import java.util.Optional;
import java.util.stream.Stream;

public class CosmosDBLayer {

    private static CosmosDBLayer instance;

    public static synchronized CosmosDBLayer getInstance() {
        if( instance != null)
            return instance;

        CosmosClient client = new CosmosClientBuilder()
                .endpoint("https://scc-backend-db.documents.azure.com:443/")
                .key("NoxlgMjT9lxW2Bq6355IlKbQ1WJybDuLiz7sw22L9GLatWMIUxYQnFsU49vkCH5n58ewhDihNgcdrwdaUE4QQw==")
                // .directMode() // connects directly to backend node
                .gatewayMode() // one more hop (needed to work within FCT)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .connectionSharingAcrossClientsEnabled(true)
                .contentResponseOnWriteEnabled(true)
                .buildClient();
        instance = new CosmosDBLayer( client);
        return instance;

    }

    private final CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer users;
    private CosmosContainer auctions;
    private CosmosContainer bids;
    private CosmosContainer questions;
    private CosmosContainer cookies;

    public CosmosDBLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if( db != null)
            return;

        db = client.getDatabase("scc-backend-database");

        // containers
        users = db.getContainer("users");
        auctions = db.getContainer("auctions");
        bids = db.getContainer("bids");
        questions = db.getContainer("questions");
        cookies = db.getContainer("cookies");
    }

    public Stream<AuctionDAO> getClosingAuctions(){
        init();
        return auctions.queryItems("SELECT * FROM auctions WHERE auctions.end_time <= GetCurrentTimestamp() AND NOT auctions.closed",
                new CosmosQueryRequestOptions(), AuctionDAO.class).stream();
    }

    public Optional<AuctionDAO> updateAuction(AuctionDAO auction)
    {
        init();
        PartitionKey key = new PartitionKey(auction.getOwnerNickname());
        return Optional.ofNullable(auctions.replaceItem(auction, auction.getAuctionID(), key, new CosmosItemRequestOptions())
                .getItem());
    }

    public Stream<AuctionDAO> getAuctionsByUser(String nickname){
        init();
        return auctions.queryItems("SELECT * FROM auctions WHERE auctions.owner_nickname=\"" + nickname + "\"",
                new CosmosQueryRequestOptions(), AuctionDAO.class).stream();
    }

    public boolean delAuctionByID(String auctionID, String owner_nickname)
    {
        init();
        PartitionKey key = new PartitionKey(owner_nickname);
        int result = auctions.deleteItem(auctionID, key, new CosmosItemRequestOptions()).getStatusCode();
        return result >= 200 && result < 300;
    }

    public Stream<String>  getAllImagesFromTable(String table){
        init();
        if(table.equals("users"))
            return users.queryItems("SELECT photo_id FROM users", new CosmosQueryRequestOptions(), String.class).stream();

        return auctions.queryItems("SELECT thumbnail_id FROM auctions", new CosmosQueryRequestOptions(), String.class).stream();
    }

    public Stream<BidDAO> getBidsByUser(String nickname) {
        init();
        return bids.queryItems("SELECT * FROM bids WHERE bids.bidder_nickname=\"" + nickname + "\"", new CosmosQueryRequestOptions(), BidDAO.class)
                .stream();
    }

    public Stream<QuestionDAO> getQuestionsAskedByUser(String nickname) {
        init();
        return questions.queryItems("SELECT * FROM questions WHERE questions.questioner=\"" + nickname + "\"", new CosmosQueryRequestOptions(), QuestionDAO.class)
                .stream();
    }

    public void updateBid(BidDAO bidDAO) {
        init();
        bids.upsertItem(bidDAO);
    }

    public void updateQuestion(QuestionDAO questionDAO) {
        init();
        questions.upsertItem(questionDAO);
    }

    public boolean deleteUserByNickname(String nickname) {
        init();
        PartitionKey key = new PartitionKey( nickname);
        int result = users.deleteItem(nickname, key, new CosmosItemRequestOptions()).getStatusCode();
        return result >= 200 && result < 300;
    }
}

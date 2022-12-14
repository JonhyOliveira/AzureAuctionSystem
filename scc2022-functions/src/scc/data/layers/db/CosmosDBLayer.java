package scc.data.layers.db;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import scc.data.models.AuctionDAO;
import scc.data.models.BidDAO;
import scc.data.models.QuestionDAO;
import scc.data.models.UserDAO;

import java.util.Optional;
import java.util.stream.Stream;

public class CosmosDBLayer implements DBLayer {

    private static CosmosDBLayer instance;

    public static synchronized CosmosDBLayer getInstance() {
        if( instance != null)
            return instance;

        CosmosClient client = new CosmosClientBuilder()
                .endpoint(System.getenv("DB_URI"))
                .key(System.getenv("DB_PKEY"))
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

        db = client.getDatabase(System.getenv("DB_NAME"));

        // containers
        users = db.getContainer("users");
        auctions = db.getContainer("auctions");
        bids = db.getContainer("bids");
        questions = db.getContainer("questions");
        cookies = db.getContainer("cookies");
    }

    @Override
    public Stream<AuctionDAO> getClosingAuctions(){
        init();
        return auctions.queryItems("SELECT * FROM auctions WHERE auctions.end_time <= GetCurrentTimestamp() AND NOT auctions.closed",
                new CosmosQueryRequestOptions(), AuctionDAO.class).stream();
    }

    @Override
    public Optional<AuctionDAO> updateAuction(AuctionDAO auction)
    {
        init();
        PartitionKey key = new PartitionKey(auction.getOwnerNickname());
        return Optional.ofNullable(auctions.replaceItem(auction, auction.getAuctionID(), key, new CosmosItemRequestOptions())
                .getItem());
    }

    @Override
    public Stream<AuctionDAO> getAuctionsByUser(String nickname){
        init();
        return auctions.queryItems("SELECT * FROM auctions WHERE auctions.owner_nickname=\"" + nickname + "\"",
                new CosmosQueryRequestOptions(), AuctionDAO.class).stream();
    }

    @Override
    public boolean delAuctionByID(String auctionID, String owner_nickname)
    {
        init();
        PartitionKey key = new PartitionKey(owner_nickname);
        int result = auctions.deleteItem(auctionID, key, new CosmosItemRequestOptions()).getStatusCode();
        return result >= 200 && result < 300;
    }

    @Override
    public Stream<String> getAllImagesFromTable(String table){
        init();
        if(table.equals("users"))
            return users.queryItems("SELECT * FROM users", new CosmosQueryRequestOptions(), UserDAO.class).stream().map(UserDAO::getPhotoId);

        return auctions.queryItems("SELECT * FROM auctions", new CosmosQueryRequestOptions(), AuctionDAO.class).stream().map(AuctionDAO::getThumbnailID);
    }

    @Override
    public Stream<BidDAO> getBidsByUser(String nickname) {
        init();
        return bids.queryItems("SELECT * FROM bids WHERE bids.bidder_nickname=\"" + nickname + "\"", new CosmosQueryRequestOptions(), BidDAO.class)
                .stream();
    }

    @Override
    public Stream<QuestionDAO> getQuestionsAskedByUser(String nickname) {
        init();
        return questions.queryItems("SELECT * FROM questions WHERE questions.questioner=\"" + nickname + "\"", new CosmosQueryRequestOptions(), QuestionDAO.class)
                .stream();
    }

    @Override
    public void updateBid(BidDAO bidDAO) {
        init();
        bids.upsertItem(bidDAO);
    }

    @Override
    public void updateQuestion(QuestionDAO questionDAO) {
        init();
        questions.upsertItem(questionDAO);
    }

    @Override
    public boolean deleteUserByNickname(String nickname) {
        init();
        PartitionKey key = new PartitionKey( nickname);
        int result = users.deleteItem(nickname, key, new CosmosItemRequestOptions()).getStatusCode();
        return result >= 200 && result < 300;
    }
}

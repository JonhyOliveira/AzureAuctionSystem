package scc.data.layers.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import scc.data.models.AuctionDAO;
import scc.data.models.BidDAO;
import scc.data.models.QuestionDAO;

import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class MongoDBLayer implements DBLayer {

    private static MongoDBLayer instance;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(MongoDBLayer.class.getName());
    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<Document> users;
    private MongoCollection<Document> auctions;
    private MongoCollection<Document> bids;
    private MongoCollection<Document> questions;
    private MongoCollection<Document> cookies;

    public static synchronized MongoDBLayer getInstance() {
        if (instance == null || instance.client == null)
            instance = new MongoDBLayer(MongoClients.create(System.getenv("DB_CONNSTRING")));

        return instance;
    }

    public MongoDBLayer(MongoClient client)
    {
        this.client = client;
        logger.warning("Connected.");
    }

    private synchronized void init() {
        if (db != null)
            return;

        db = client.getDatabase(System.getenv("DB_NAME"));
        users = db.getCollection("users");
        auctions = db.getCollection("auctions");
        bids = db.getCollection("bids");
        questions = db.getCollection("questions");
        cookies = db.getCollection("cookies");
    }

    @Override
    public Stream<AuctionDAO> getClosingAuctions() {
        init();
        return null;
    }

    @Override
    public Optional<AuctionDAO> updateAuction(AuctionDAO auction) {
        init();
        return Optional.empty();
    }

    @Override
    public Stream<AuctionDAO> getAuctionsByUser(String nickname) {
        init();
        return null;
    }

    @Override
    public boolean delAuctionByID(String auctionID, String owner_nickname) {
        init();
        return false;
    }

    @Override
    public Stream<String> getAllImagesFromTable(String table) {
        init();
        return null;
    }

    @Override
    public Stream<BidDAO> getBidsByUser(String nickname) {
        init();
        return null;
    }

    @Override
    public Stream<QuestionDAO> getQuestionsAskedByUser(String nickname) {
        init();
        return null;
    }

    @Override
    public void updateBid(BidDAO bidDAO) {
        init();

    }

    @Override
    public void updateQuestion(QuestionDAO questionDAO) {
        init();

    }

    @Override
    public boolean deleteUserByNickname(String nickname) {
        init();
        return false;
    }
}

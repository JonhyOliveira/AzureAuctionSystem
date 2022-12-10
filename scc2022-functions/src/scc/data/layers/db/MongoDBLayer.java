package scc.data.layers.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import scc.data.models.AuctionDAO;
import scc.data.models.BidDAO;
import scc.data.models.QuestionDAO;
import scc.data.models.UserDAO;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
        return StreamSupport.stream(auctions.find(Filters.eq(AuctionDAO.StatusKey, AuctionDAO.Status.CLOSING.name()))
                .map(Document::toJson).spliterator(), false)
                .map(s -> {
                    try{
                        return mapper.readValue(s, AuctionDAO.class);
                    } catch (JsonProcessingException e){
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public Optional<AuctionDAO> updateAuction(AuctionDAO newAuction) {
        init();
        try {
            auctions.replaceOne(Filters.eq(AuctionDAO.ID, newAuction.getAuctionID()),
                    Document.parse(mapper.writeValueAsString(newAuction)));
            return Optional.of(newAuction);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public Stream<AuctionDAO> getAuctionsByUser(String nickname) {
        init();
        return StreamSupport.stream(auctions.find(Filters.eq(AuctionDAO.OwnerKey, nickname))
                        .map(Document::toJson).spliterator(), false)
                .map(s -> {
                    try{
                        return mapper.readValue(s, AuctionDAO.class);
                    }catch (JsonProcessingException e){
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public boolean delAuctionByID(String auctionID, String owner_nickname) {
        init();
        return auctions.deleteOne(Filters.and(Filters.eq(AuctionDAO.ID, auctionID),
        Filters.eq(AuctionDAO.OwnerKey, owner_nickname))).getDeletedCount() > 0;
    }

    @Override
    public Stream<String> getAllImagesFromTable(String table) {
        init();
        return StreamSupport.stream(db.getCollection(table).find().map(Document::toJson).spliterator(), false)
                .map(s -> {
                    try{
                        return mapper.readTree(s).get("image").asText();
                    } catch (JsonProcessingException e){
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public Stream<BidDAO> getBidsByUser(String nickname) {
        init();
        return StreamSupport.stream(bids.find(Filters.eq(BidDAO.Bidder, nickname))
                .map(Document::toJson).spliterator(), false)
                .map(s-> {
                    try{
                        return mapper.readValue(s, BidDAO.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                });
    }

    @Override
    public Stream<QuestionDAO> getQuestionsAskedByUser(String nickname) {
        init();
        return StreamSupport.stream(questions.find(Filters.eq(QuestionDAO.Questioner, nickname))
                .map(Document::toJson).spliterator(), false)
                .map(s -> {
                    try{
                        return mapper.readValue(s, QuestionDAO.class);
                    } catch (JsonProcessingException e){
                        return null;
                    }
                });
    }

    @Override
    public void updateBid(BidDAO bid) {
        init();
        try{
            bids.replaceOne(Filters.eq(BidDAO.ID, bid.getId()),
                    Document.parse(mapper.writeValueAsString(bid)));
        } catch (JsonProcessingException ignored){}
    }

    @Override
    public void updateQuestion(QuestionDAO question) {
        init();
        try{
            questions.replaceOne(Filters.eq(QuestionDAO.ID, question.getId()),
                    Document.parse(mapper.writeValueAsString(question)));
        } catch (JsonProcessingException e){
        }
    }

    @Override
    public boolean deleteUserByNickname(String nickname) {
        init();
        return users.deleteOne(Filters.eq(UserDAO.ID, nickname)).getDeletedCount() > 0;
    }
}

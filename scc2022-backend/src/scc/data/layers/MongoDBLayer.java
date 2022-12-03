package scc.data.layers;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.json.JsonObject;
import scc.data.models.*;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class MongoDBLayer implements DBLayer {
    private static MongoDBLayer instance;
    private static final ObjectMapper mapper = new ObjectMapper();
    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<Document> users;
    private MongoCollection<Document> auctions;
    private MongoCollection<Document> bids;
    private MongoCollection<Document> questions;
    private MongoCollection<Document> cookies;

    public static MongoDBLayer getInstance() {
        if (instance == null || instance.client == null)
            instance = new MongoDBLayer(MongoClients.create(System.getenv("DB_CONNSTRING")));

        return instance;
    }

    public MongoDBLayer(MongoClient client)
    {
        this.client = client;
    }

    private synchronized void init() {
        if (db != null)
            return;

        db = client.getDatabase(System.getenv("DB_NAME"));
        Logger.getLogger(this.getClass().getName()).info("Connected");
        users = db.getCollection("users");
        auctions = db.getCollection("auctions");
        bids = db.getCollection("bids");
        questions = db.getCollection("questions");
        cookies = db.getCollection("cookies");
    }

    @Override
    public UserDAO updateUser(UserDAO newUser) {
        init();
        try {
            users.replaceOne(Filters.eq("_id", newUser.getNickname()),
                    Document.parse(mapper.writeValueAsString(newUser)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean delUserByNick(String nickname) {
        init();
        return Objects.nonNull(users.findOneAndDelete(Filters.eq("_id", nickname)));
    }

    @Override
    public Optional<UserDAO> delUser(UserDAO user) {
        init();
        return Optional.ofNullable(users.findOneAndDelete(Filters.eq("_id", user.getNickname())))
                .map(Document::toJson)
                .map(s -> {
                    try {
                        return mapper.readValue(s, UserDAO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public UserDAO putUser(UserDAO user) {
        init();
        try {
            users.insertOne(Document.parse(mapper.writeValueAsString(user)));
            return user;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserDAO> getUserByNick(String nickname) {
        init();
        return Optional.of(users.find(Filters.eq("_id", nickname)))
                .map(documents -> documents.limit(1).iterator())
                .filter(MongoCursor::hasNext)
                .map(MongoCursor::next)
                .map(Document::toJson)
                .map(s -> {
                    try {
                        return mapper.readValue(s, UserDAO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public Stream<UserDAO> getUsers() {
        init();
        return null;
    }

    @Override
    public Optional<AuctionDAO> getAuctionByID(String auctionID) {
        init();
        return Optional.empty();
    }

    @Override
    public Optional<AuctionDAO> putAuction(AuctionDAO auction) {
        init();
        return Optional.empty();
    }

    @Override
    public Optional<AuctionDAO> updateAuction(AuctionDAO auction) {
        init();
        return Optional.empty();
    }

    @Override
    public boolean delAuctionByID(String auctionID, String owner_nickname) {
        init();
        return false;
    }

    @Override
    public Stream<BidDAO> getBidsByAuctionID(String auctionID) {
        init();
        return null;
    }

    @Override
    public Stream<BidDAO> getTopBidsByAuctionID(String auctionID, Long n) {
        init();
        return null;
    }

    @Override
    public Optional<BidDAO> putBid(BidDAO bid) {
        init();
        return Optional.empty();
    }

    @Override
    public Stream<QuestionDAO> getQuestionsByAuctionID(String auctionID) {
        init();
        return null;
    }

    @Override
    public Stream<AuctionDAO> getAuctionsByUser(String nickname) {
        init();
        return null;
    }

    @Override
    public Stream<AuctionDAO> getAuctionsClosingInXMins(long x) {
        init();
        return null;
    }

    @Override
    public Optional<QuestionDAO> putQuestion(QuestionDAO question) {
        init();
        return Optional.empty();
    }

    @Override
    public Optional<QuestionDAO> updateQuestion(QuestionDAO question) {
        init();
        return Optional.empty();
    }

    @Override
    public Optional<QuestionDAO> getQuestionByID(String questionId) {
        init();
        return Optional.empty();
    }

    @Override
    public void storeCookie(String key, String value) {
        init();

    }

    @Override
    public Optional<String> getCookie(String key) {
        init();
        return Optional.empty();
    }

    @Override
    public void deleteCookie(String key) {
        init();

    }
}

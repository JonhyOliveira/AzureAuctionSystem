package scc.data.layers.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;

import org.bson.Document;
import scc.data.models.*;
import scc.session.Session;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
        logger.info("Connected");
        users = db.getCollection("users");
        auctions = db.getCollection("auctions");
        bids = db.getCollection("bids");
        questions = db.getCollection("questions");
        cookies = db.getCollection("cookies");
    }

    @Override
    public Optional<UserDAO> updateUser(UserDAO newUser) {
        init();
        try {
            users.replaceOne(Filters.eq(UserDAO.ID, newUser.getNickname()),
                    Document.parse(mapper.writeValueAsString(newUser)));
            return Optional.of(newUser);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean delUserByNick(String nickname) {
        init();
        return users.deleteOne(Filters.eq("_id", nickname)).getDeletedCount() > 0;
    }

    @Override
    public boolean delUser(UserDAO user) {
        init();
        return Optional.ofNullable(users.findOneAndDelete(Filters.eq("_id", user.getNickname())))
                .map(Document::toJson)
                .map(s -> {
                    try {
                        return mapper.readValue(s, UserDAO.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                }).isPresent();
    }

    @Override
    public Optional<UserDAO> putUser(UserDAO user) {
        init();
        try {
            users.insertOne(Document.parse(mapper.writeValueAsString(user)));
            return Optional.ofNullable(user);
        } catch (JsonProcessingException e) {
            return Optional.empty();
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
        return StreamSupport.stream(users.find().map(Document::toJson).spliterator(), false)
                .map(s -> {
                    try {
                        return mapper.readValue(s, UserDAO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public Optional<AuctionDAO> getAuctionByID(String auctionID) {
        init();
        return Optional.of(auctions.find(Filters.eq("_id", auctionID)))
                .map(documents -> documents.limit(1).iterator())
                .filter(MongoCursor::hasNext)
                .map(MongoCursor::next)
                .map(Document::toJson)
                .map(s -> {
                    try {
                        return mapper.readValue(s, AuctionDAO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public Optional<AuctionDAO> putAuction(AuctionDAO auction) {
        init();
        try {
            auctions.insertOne(Document.parse(mapper.writeValueAsString(auction)));
            return Optional.of(auction);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuctionDAO> updateAuction(AuctionDAO auction) {
        init();
        try {
            if (auctions.replaceOne(Filters.eq(AuctionDAO.ID, auction.getAuctionID()),
                    Document.parse(mapper.writeValueAsString(auction))).getModifiedCount() > 0)
                return Optional.of(auction);
        } catch (JsonProcessingException ignored) {
        }

        return Optional.empty();
    }

    @Override
    public boolean delAuctionByID(String auctionID, String owner_nickname) {
        init();
        return auctions.deleteOne(Filters.and(Filters.eq(AuctionDAO.ID, auctionID),
                Filters.eq(AuctionDAO.OwnerKey, owner_nickname))).getDeletedCount() > 0;
    }

    @Override
    public Stream<BidDAO> getBidsByAuctionID(String auctionID) {
        init();
        return StreamSupport.stream(bids.find(Filters.eq(BidDAO.AuctionID, auctionID))
                        .map(Document::toJson).spliterator(), false)
                .map(s -> {
                    try {
                        return mapper.readValue(s, BidDAO.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public Stream<BidDAO> getTopBidsByAuctionID(String auctionID, Long n) {
        init();
        return StreamSupport.stream(bids.find(Filters.eq(BidDAO.AuctionID, auctionID))
                        .sort(Sorts.descending(BidDAO.Amount))
                        .limit(n.intValue())
                        .map(Document::toJson).spliterator(), false)
                .map(s -> {
                    try {
                        return mapper.readValue(s, BidDAO.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public Optional<BidDAO> putBid(BidDAO bid) {
        init();
        try {
            bids.insertOne(Document.parse(mapper.writeValueAsString(bid)));
            return Optional.of(bid);
        } catch (JsonProcessingException ignored) {
        }

        return Optional.empty();
    }

    @Override
    public Stream<QuestionDAO> getQuestionsByAuctionID(String auctionID) {
        init();
        return StreamSupport.stream(questions.find(Filters.eq(AuctionDAO.ID, auctionID)).map(Document::toJson).spliterator(), false)
                .map(s -> {
                    try {
                        return mapper.readValue(s, QuestionDAO.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public Stream<AuctionDAO> getAuctionsByUser(String nickname) {
        init();
        return StreamSupport.stream(auctions.find(Filters.eq(AuctionDAO.OwnerKey, nickname)).map(Document::toJson).spliterator(), false)
                .map(s -> {
                    try {
                        return mapper.readValue(s, AuctionDAO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public Stream<AuctionDAO> getAuctionsClosingInXMins(long x) {
        init();
        return StreamSupport.stream(auctions.find(Filters.lte(AuctionDAO.EndKey, TimeUnit.MINUTES.toMillis(x))).map(Document::toJson).spliterator(), false)
                .map(s -> {
                    try {
                        return mapper.readValue(s, AuctionDAO.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public Optional<QuestionDAO> putQuestion(QuestionDAO question) {
        init();
        try {
            questions.insertOne(Document.parse(mapper.writeValueAsString(question)));
            return Optional.of(question);
        } catch (JsonProcessingException ignored) {
        }

        return Optional.empty();
    }

    @Override
    public Optional<QuestionDAO> updateQuestion(QuestionDAO question) {
        init();
        try {
            if (questions.replaceOne(Filters.eq(QuestionDAO.ID, question.getId()),
                    Document.parse(mapper.writeValueAsString(question)),
                    new ReplaceOptions().upsert(true)).getModifiedCount() > 0)
                return Optional.of(question);
        } catch (JsonProcessingException ignored) {
        }

        return Optional.empty();
    }

    @Override
    public Optional<QuestionDAO> getQuestionByID(String questionId) {
        init();
        return Optional.of(questions.find(Filters.eq(QuestionDAO.ID, questionId)))
                .map(documents -> documents.limit(1).iterator())
                .filter(MongoCursor::hasNext)
                .map(MongoCursor::next)
                .map(Document::toJson)
                .map(s -> {
                    try {
                        return mapper.readValue(s, QuestionDAO.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                });
    }

    @Override
    public Optional<String> storeCookie(String key, String value) {
        init();
        try {
            if (cookies.replaceOne(Filters.eq(CookieDAO.ID, key),
                    Document.parse(mapper.writeValueAsString(new CookieDAO(key, value, Session.VALIDITY_SECONDS))),
                    new ReplaceOptions().upsert(true)).getModifiedCount() > 0)
                return Optional.of(value);
        } catch (JsonProcessingException ignored) {
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> getCookie(String key) {
        init();
        return Optional.of(cookies.find(Filters.eq(CookieDAO.ID, key)))
                .map(documents -> documents.limit(1).iterator())
                .filter(MongoCursor::hasNext)
                .map(MongoCursor::next)
                .map(Document::toJson)
                .map(s -> {
                    try {
                        return mapper.readValue(s, CookieDAO.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .map(CookieDAO::getValue);
    }

    @Override
    public boolean deleteCookie(String key) {
        init();
        return cookies.deleteOne(Filters.eq(CookieDAO.ID, key)).getDeletedCount() > 0;
    }
}

package scc.data;

import redis.clients.jedis.Jedis;
import scc.data.layers.*;
import scc.data.layers.db.DBLayer;
import scc.data.layers.db.MongoDBLayer;
import scc.data.layers.storage.File;
import scc.data.layers.storage.FileSystemStorageLayer;
import scc.data.layers.storage.StorageLayer;
import scc.data.models.AuctionDAO;
import scc.data.models.BidDAO;
import scc.data.models.QuestionDAO;
import scc.data.models.UserDAO;

import jakarta.ws.rs.core.NewCookie;
import scc.session.Session;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class manages
 */
public class DataProxy {

    private static final boolean USE_CACHE = !Boolean.parseBoolean(System.getenv("DISABLE_CACHE"));
    private static final DBLayer dbLayer;
    private static final StorageLayer storageLayer = FileSystemStorageLayer.getInstance();

    private static final RedisCacheLayer cachingLayer = RedisCacheLayer.getInstance();
    private static final SearchLayer searchLayer;

    static {
        MongoDBLayer mongoDBLayer = MongoDBLayer.getInstance();

        dbLayer = mongoDBLayer;
        searchLayer = mongoDBLayer;
    }

    private static DataProxy instance;

    public static DataProxy getInstance() {
        if (instance == null)
            instance = new DataProxy();

        return instance;
    }

    /**
     * Creates a user
     * @return the created user, if possible to create
     */
    public Optional<User> createUser(User user)
    {
        if (dbLayer.getUserByNick(user.getNickname()).isPresent())
            return Optional.empty();

        UserDAO u = dbLayer.putUser(new UserDAO(user)).get();

        if (USE_CACHE)
            cachingLayer.putOnCache("u:" + u.getNickname(), u);

        return Optional.of(u)
                .map(UserDAO::toUser);
    }

    /**
     * Updates the information of an user
     * @param nickname the user's nickname
     * @param newUser the new user info
     * @return the updated user, if found
     */
    public Optional<User> updateUserInfo(String nickname, User newUser)
    {
        newUser.setNickname(nickname);
        UserDAO u = dbLayer.updateUser(new UserDAO(newUser.hashPwd())).get();

        if (USE_CACHE)
            cachingLayer.putOnCache("u:" + u.getNickname(), u, 60);

        return Optional.of(u)
                .map(UserDAO::toUser);
    }

    /**
     * @return the user associated with the given nickname, if found
     */
    public Optional<User> getUser(String nickname)
    {

        UserDAO userObject = null;

        if (USE_CACHE)
            userObject = cachingLayer.getFromCache("u:" + nickname, UserDAO.class);

        if (userObject != null)
            return Optional.of(userObject).map(UserDAO::toUser);

        userObject = dbLayer.getUserByNick(nickname)
                .filter(userDAO -> !userDAO.isToDelete()).orElse(null);

        if (userObject != null && USE_CACHE)
            cachingLayer.putOnCache("u:" + nickname, userObject, 60);

        return Optional.ofNullable(userObject).map(UserDAO::toUser);
    }

    /**
     * Deletes a user
     * @param nickname the user nickname
     */
    public void deleteUser(String nickname)
    {
        getUser(nickname).ifPresent(user -> {
            UserDAO dao = new UserDAO(user);
            dao.setToDelete(true);
            dbLayer.updateUser(dao);
            if (USE_CACHE)
                cachingLayer.deleteFromCache("u:" + user.getNickname());
            deleteSession(user.getNickname());

            cachingLayer.insertInSet("gc:users", user.getNickname());
        });
    }

    /**
     * Creates an auction
     * @return the created auction
     */
    public Optional<Auction> createAuction(Auction auction)
    {
        return dbLayer.putAuction(new AuctionDAO(auction))
                .map(AuctionDAO::toAuction);
    }

    /**
     * @return the auction associated with the given auction ID
     */
    public Optional<Auction> getAuction(String auctionID)
    {
        return dbLayer.getAuctionByID(auctionID)
                .map(AuctionDAO::toAuction);
    }

    /**
     * Updates an auction
     * @param auctionID the id of the auction
     * @param auction the auction data, auctionID is ignored and
     *                everything else is taken as is
     * @return the updated auction
     */
    public Optional<Auction> updateAuctionInfo(String auctionID, Auction auction)
    {
        auction.setAuctionID(auctionID);

        return dbLayer.updateAuction(new AuctionDAO(auction))
                .map(AuctionDAO::toAuction);
    }

    /**
     * Deletes an auction
     * @param auctionID the id of the auction
     */
    public void deleteAuction(String auctionID, String owner_nickname)
    {
        dbLayer.delAuctionByID(auctionID, owner_nickname);
    }

    /**
     * Gets the bids of an auction
     * @param auctionID the id of the auction
     * @return a list of bids
     */
    public List<Bid> getAuctionBids(String auctionID) {
        return dbLayer.getBidsByAuctionID(auctionID)
                .map(BidDAO::toBid)
                .collect(Collectors.toList());
    }

    /**
     * Returns the higest bid in an auction
     * @param auctionID the id of the auction
     * @return the highest bid
     */
    public Optional<Bid> getHighestBid(String auctionID) {
        return dbLayer.getTopBidsByAuctionID(auctionID, 1L)
                .map(BidDAO::toBid)
                .findFirst();
    }

    /**
     * Executes a bid under an auction
     * @param auctionId the id of auction to put the bid under
     * @param bid the bid details
     * @return the created bid
     */
    public Optional<Bid> executeBid(String bidId, String auctionId, Bid bid)
    {
        return dbLayer.putBid(new BidDAO(bidId, auctionId, bid))
                .map(BidDAO::toBid);
    }

    /**
     * Show the list of questions under an auction
     * @param auctionID the id of the auction
     * @return the list of question under the auction
     */
    public List<Question> getAuctionQuestions(String auctionID){
        return dbLayer.getQuestionsByAuctionID(auctionID)
                .map(QuestionDAO::toQuestion)
                .collect(Collectors.toList());
    }

    /**
     * Creates a question under an auction
     * @param auctionId the id of the auction
     * @param question the question details
     * @return the details of the created question
     */
    public Optional<Question> createQuestion(String auctionId, Question question) {
        return dbLayer.putQuestion(new QuestionDAO(auctionId, question))
                .map(QuestionDAO::toQuestion);
    }

    /**
     * @param questionId the id of the question
     * @return the question with the given id
     */
    public Optional<Question> getQuestion(String questionId) {
        return dbLayer.getQuestionByID(questionId)
                .map(QuestionDAO::toQuestion);
    }

    /**
     * Updates a question under an auction
     * @param auctionId the id of the auction the question is under
     * @param questionID the id of the question
     * @param question the new question details
     * @return the updated question
     */
    public Optional<Question> updateQuestion(String auctionId, String questionID, Question question) {
        question.setQuestionID(questionID);

        return dbLayer.updateQuestion(new QuestionDAO(auctionId, question))
                .map(QuestionDAO::toQuestion);
    }

    /**
     * Gets auctions of a user
     * @param nickname nickname of the owner of the auctions
     * @return list of user's auctions
     */
    public List<Auction> getAuctionsByUser(String nickname){
        return dbLayer.getAuctionsByUser(nickname)
                .map(AuctionDAO::toAuction)
                .collect(Collectors.toList());
    }

    /**
     * Gets auctions about to be closed
     * @return list of auctions which are less or equal to 5 minutes away of getting closed
     */
    public List<Auction> getAuctionsClosingInXMinutes(long x){
        return dbLayer.getAuctionsClosingInXMins(x)
                .map(AuctionDAO::toAuction)
                .collect(Collectors.toList());
    }

    /**
     * Downlaods an image from the shared storage
     * @param imageID the id of the image
     * @return the byte contents of the image
     */
    public byte[] downloadImage(String imageID)
    {
        return storageLayer.downloadFile(imageID);
    }

    /**
     * Checks if an iamge exists in the shared storage
     * @param imageID the id of the image
     * @return true if it exists, false otherwise
     */
    public boolean doesImageExist(String imageID)
    {
        return storageLayer.fileExists(imageID);
    }

    /**
     * Uploads an image to the shared storage
     * @param imageID the id to retrieve the image afterwards
     * @param contents the contents of the file
     */
    public void uploadImage(String imageID, byte[] contents) {
        storageLayer.createFile(imageID, contents);
    }

    public void deleteImage(String imageID) {
        storageLayer.deleteFile(imageID);
    }

    public List<String> listImages() {
        return storageLayer.listFiles().map(File::fileID).collect(Collectors.toList());
    }

    public void storeSession(NewCookie cookie, String nickname) {
        if (USE_CACHE)
            cachingLayer.putOnCache( Session.COOKIE_NAME + ":" + nickname, cookie.getValue(), Session.VALIDITY_SECONDS);

        dbLayer.storeCookie(Session.COOKIE_NAME + ":" + nickname , cookie.getValue());
    }

    public Optional<Session> getSession(String nickname) {
        Optional<String> cookieID = Optional.ofNullable(USE_CACHE ?
                cachingLayer.getFromCache(Session.COOKIE_NAME + ":" + nickname, String.class) : null);

        if (cookieID.isEmpty())
            cookieID = dbLayer.getCookie(Session.COOKIE_NAME + ":" + nickname);

        return cookieID.map(s -> new Session(s, nickname));
    }

    public void deleteSession(String nickname) {
        if (USE_CACHE)
            cachingLayer.deleteFromCache(Session.COOKIE_NAME + ":" + nickname);
        dbLayer.deleteCookie(Session.COOKIE_NAME + ":" + nickname);
    }

    public List<Auction> searchAuctions(String queryString) {
        return searchLayer.findAuction(queryString).map(AuctionDAO::toAuction).collect(Collectors.toList());
    }

    public List<Auction> getRecentlyUpdatedAuctions() {
        Set<String> recentlyUpdatedIDs = new HashSet<>();

        try (Jedis jedis = RedisCacheLayer.getCachePool().getResource()) {
            String id;
            for (long i = 0; (id = jedis.lindex("recentlyUpdatedAuctions", i)) != null; i++)
                recentlyUpdatedIDs.add(id);
        }

        return recentlyUpdatedIDs.stream()
                .map(this::getAuction)
                .map(auction -> auction.orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

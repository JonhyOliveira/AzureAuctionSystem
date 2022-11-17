package scc.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.core.Cookie;
import redis.clients.jedis.JedisPool;
import scc.data.layers.BlobStorageLayer;
import scc.data.layers.CognitiveSearchLayer;
import scc.data.layers.CosmosDBLayer;
import scc.data.layers.RedisCacheLayer;
import scc.data.models.AuctionDAO;
import scc.data.models.BidDAO;
import scc.data.models.QuestionDAO;
import scc.data.models.UserDAO;

import jakarta.ws.rs.core.NewCookie;
import scc.session.SessionTemp;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class manages
 */
public class DataProxy {

    private static final boolean USE_CACHE = true;
    private static final CosmosDBLayer dbLayer = CosmosDBLayer.getInstance();
    private static final RedisCacheLayer redisLayer = USE_CACHE ? RedisCacheLayer.getInstance() : null;
    private static final BlobStorageLayer blobStorage = BlobStorageLayer.getInstance();
    private static final CognitiveSearchLayer searchLayer = CognitiveSearchLayer.getInstance();

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
        UserDAO u = dbLayer.putUser(new UserDAO(user)).getItem();

        redisLayer.putOnCache("u:" + u.getNickname(), u);

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
        UserDAO u = dbLayer.updateUser(new UserDAO(newUser.hashPwd())).getItem();

        redisLayer.putOnCache("u:" + u.getNickname(), u);

        return Optional.of(u)
                .map(UserDAO::toUser);
    }

    /**
     * @return the user associated with the given nickname, if found
     */
    public Optional<User> getUser(String nickname)
    {
        UserDAO userObject = redisLayer.getFromCache("u:" + nickname, UserDAO.class);

        if (userObject != null)
            return Optional.of(userObject.toUser());

        return dbLayer.getUserByNick(nickname)
                .map(UserDAO::toUser);
    }

    /**
     * Deletes a user
     * @param nickname the user nickname
     */
    public void deleteUser(String nickname)
    {
        redisLayer.deleteFromCache("u:" + nickname);
        dbLayer.delUserByNick(nickname);
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
    public List<Auction> getClosingAuctions(){
        return dbLayer.getClosingAuctions()
                .map(AuctionDAO::toAuction)
                .collect(Collectors.toList());
    }

    /**
     * Downlaods a file from the shared storage
     * @param fileID the id of the file
     * @return the byte contents of the blob
     */
    public byte[] downloadFile(String fileID)
    {
        return blobStorage.downloadBlob(fileID);
    }

    /**
     * Checks if a file exists in the shared storage
     * @param fileID the id of the file
     * @return true if it exists, false otherwise
     */
    public boolean doesFileExist(String fileID)
    {
        return blobStorage.blobExists(fileID);
    }

    /**
     * Uploads a file to the shared storage
     * @param fileID the id to store the file under
     * @param contents the contents of the file
     */
    public void uploadFile(String fileID, byte[] contents) {
        blobStorage.createBlob(fileID, contents);
    }

    public List<String> listFiles() {
        return blobStorage.listFiles().collect(Collectors.toList());
    }

    public void storeCookie(NewCookie cookie, String nickname) {
        if (USE_CACHE)
            redisLayer.putOnCache("session:" + nickname, cookie.getValue(), SessionTemp.VALIDITY_SECONDS);

        dbLayer.storeCookie(SessionTemp.COOKIE_NAME + ":" + nickname , cookie.getValue());
    }

    public Optional<SessionTemp> getSession(String nickname) {
        Optional<String> cookieID = Optional.ofNullable(redisLayer.getFromCache("session:" + nickname, String.class));

        if (cookieID.isEmpty())
            cookieID = dbLayer.getCookie(SessionTemp.COOKIE_NAME + ":" + nickname);

        return cookieID.map(s -> new SessionTemp(s, nickname));
    }

    public List<Auction> searchAuctions(String queryString) {
        return searchLayer.findAuction(queryString).map(AuctionDAO::toAuction).collect(Collectors.toList());
    }
}

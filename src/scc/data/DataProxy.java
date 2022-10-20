package scc.data;

import jakarta.ws.rs.ServiceUnavailableException;
import redis.clients.jedis.JedisPool;
import scc.data.layers.CosmosDBLayer;
import scc.data.layers.RedisCacheLayer;
import scc.data.models.AuctionDAO;
import scc.data.models.BidDAO;
import scc.data.models.QuestionDAO;
import scc.data.models.UserDAO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataProxy {

    private static final CosmosDBLayer dbLayer = CosmosDBLayer.getInstance();
    private static final JedisPool jedisPool = RedisCacheLayer.getCachePool();

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
        return Optional.ofNullable(dbLayer.putUser(new UserDAO(user)).getItem())
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
        return Optional.ofNullable(dbLayer.updateUser(new UserDAO(newUser.hashPwd())).getItem())
                .map(UserDAO::toUser);
    }

    /**
     * @return the user associated with the given nickname, if found
     */
    public Optional<User> getUser(String nickname)
    {
        return dbLayer.getUserByNick(nickname)
                .stream()
                .findFirst()
                .map(UserDAO::toUser);
    }

    public void deleteUser(String nickname)
    {
        dbLayer.delUserByNick(nickname);
    }

    /**
     * Creates an auction
     * @return the created auction
     */
    public Optional<Auction> createAuction(Auction auction)
    {
        return Optional.ofNullable(dbLayer.putAuction(new AuctionDAO(auction)).getItem())
                .map(AuctionDAO::toAuction);
    }

    /**
     * @return the auction associated with the given auction ID
     */
    public Optional<Auction> getAuction(String auctionID)
    {
        return dbLayer.getAuctionByID(auctionID)
                .stream()
                .findFirst()
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

        return Optional.ofNullable(dbLayer.updateAuction(new AuctionDAO(auction)).getItem())
                .map(AuctionDAO::toAuction);
    }

    public void deleteAuction(String auctionID)
    {
        dbLayer.delAuctionByID(auctionID);
    }

    public List<Bid> getAuctionBids(String auctionID) {
        return dbLayer.getBidsByAuctionID(auctionID)
                .stream()
                .map(BidDAO::toBid)
                .collect(Collectors.toList());
    }

    public Optional<Bid> getHighestBid(String auctionID) {
        return dbLayer.getTopBidsByAuctionID(auctionID, 1L)
                .stream()
                .map(BidDAO::toBid)
                .findFirst();
    }

    public Optional<Bid> executeBid(String auctionId, Bid bid)
    {
        return Optional.ofNullable(dbLayer.putBid(new BidDAO(auctionId, bid)).getItem())
                .map(BidDAO::toBid);
    }

    public List<Question> getAuctionQuestions(String auctionID){
        return dbLayer.getQuestionsByAuctionID(auctionID)
                .stream()
                .map(QuestionDAO::toQuestion)
                .collect(Collectors.toList());
    }

    public Optional<Question> createQuestion(String auctionId, Question question) {
        return Optional.ofNullable(dbLayer.putQuestion(new QuestionDAO(auctionId, question)).getItem())
                .map(QuestionDAO::toQuestion);
    }

    public Optional<Question> getQuestion(String questionId) {
        return dbLayer.getQuestionByID(questionId)
                .stream()
                .findFirst()
                .map(QuestionDAO::toQuestion);
    }

    public Optional<Question> updateQuestion(String auctionId, String questionID, Question question) {
        question.setQuestionID(questionID);

        return Optional.ofNullable(dbLayer.updateQuestion(new QuestionDAO(auctionId, question)).getItem())
                .map(QuestionDAO::toQuestion);
    }
    
}

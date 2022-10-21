package scc.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ServiceUnavailableException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import scc.data.layers.CosmosDBLayer;
import scc.data.layers.RedisCacheLayer;
import scc.data.models.AuctionDAO;
import scc.data.models.BidDAO;
import scc.data.models.QuestionDAO;
import scc.data.models.UserDAO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataProxy {

    private static final CosmosDBLayer dbLayer = CosmosDBLayer.getInstance();
    private static final JedisPool jedisPool = RedisCacheLayer.getCachePool();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final long DEFAULT_EXPIRATION = 3600;  //  TIME FOR AN OBJECT TO EXPIRE FROM CACHE

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

        try{
            jedisPool.getResource().setex("user:"+u.getNickname(), DEFAULT_EXPIRATION ,mapper.writeValueAsString(u));
        } catch (JsonProcessingException ignored){
            //TODO DAR HANDLING DA EXCEPTION
        }

        return Optional.ofNullable(u)
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

        try{
            jedisPool.getResource().setex("user:"+u.getNickname(), DEFAULT_EXPIRATION ,mapper.writeValueAsString(u));
        } catch (JsonProcessingException ignored){
            //TODO DAR HANDLING DA EXCEPTION
        }

        return Optional.ofNullable(u)
                .map(UserDAO::toUser);
    }

    /**
     * @return the user associated with the given nickname, if found
     */
    public Optional<User> getUser(String nickname)
    {
        UserDAO userObject = null;

        try{
            String user = jedisPool.getResource().get("user:" + nickname);
            userObject = mapper.readValue(user, UserDAO.class);
        } catch (JsonProcessingException ignored){
            //TODO DAR HANDLING DA EXCEPTION
        }

        if(Objects.nonNull(userObject))    //Verify if exists in cache
            return Optional.of(userObject).map(UserDAO::toUser);

        return dbLayer.getUserByNick(nickname)
                .stream()
                .findFirst()
                .map(UserDAO::toUser);
    }

    /**
     * Deletes a user
     * @param nickname the user nickname
     */
    public void deleteUser(String nickname)
    {
        jedisPool.getResource().del("user:"+nickname);

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

    /**
     * Deletes an auction
     * @param auctionID the id of the auction
     */
    public void deleteAuction(String auctionID)
    {
        dbLayer.delAuctionByID(auctionID);
    }

    /**
     * Gets the bids of an auction
     * @param auctionID the id of the auction
     * @return a list of bids
     */
    public List<Bid> getAuctionBids(String auctionID) {
        return dbLayer.getBidsByAuctionID(auctionID)
                .stream()
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
                .stream()
                .map(BidDAO::toBid)
                .findFirst();
    }

    /**
     * Executes a bid under an auction
     * @param auctionId the id of auction to put the bid under
     * @param bid the bid details
     * @return the created bid
     */
    public Optional<Bid> executeBid(String auctionId, Bid bid)
    {
        return Optional.ofNullable(dbLayer.putBid(new BidDAO(auctionId, bid)).getItem())
                .map(BidDAO::toBid);
    }

    /**
     * Show the list of questions under an auction
     * @param auctionID the id of the auction
     * @return the list of question under the auction
     */
    public List<Question> getAuctionQuestions(String auctionID){
        return dbLayer.getQuestionsByAuctionID(auctionID)
                .stream()
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
        return Optional.ofNullable(dbLayer.putQuestion(new QuestionDAO(auctionId, question)).getItem())
                .map(QuestionDAO::toQuestion);
    }

    /**
     * @param questionId the id of the question
     * @return the question with the given id
     */
    public Optional<Question> getQuestion(String questionId) {
        return dbLayer.getQuestionByID(questionId)
                .stream()
                .findFirst()
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

        return Optional.ofNullable(dbLayer.updateQuestion(new QuestionDAO(auctionId, question)).getItem())
                .map(QuestionDAO::toQuestion);
    }

    /**
     * Gets auctions of a user
     * @param nickname nickname of the owner of the auctions
     * @return list of user's auctions
     */
    public List<Auction> getAuctionsByUser(String nickname){
        return dbLayer.getAuctionsByUser(nickname)
                .stream()
                .map(AuctionDAO::toAuction)
                .collect(Collectors.toList());
    }

    /**
     * Gets auctions about to be closed
     * @return list of auctions which are less or equal to 5 minutes away of getting closed
     */
    public List<Auction> getClosingAuctions(){
        return dbLayer.getClosingAuctions()
                .stream()
                .map(AuctionDAO::toAuction)
                .collect(Collectors.toList());
    }

    
}

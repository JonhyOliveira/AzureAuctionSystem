package scc.data.layers.db;

import scc.data.models.AuctionDAO;
import scc.data.models.BidDAO;
import scc.data.models.QuestionDAO;
import scc.data.models.UserDAO;

import java.util.Optional;
import java.util.stream.Stream;

public interface DBLayer {
    Optional<UserDAO> updateUser(UserDAO newUser);

    boolean delUserByNick(String nickname);
    
    boolean delUser(UserDAO user);

    Optional<UserDAO> putUser(UserDAO user);

    Optional<UserDAO> getUserByNick(String nickname);

    @SuppressWarnings("unused")
    Stream<UserDAO> getUsers();

    Optional<AuctionDAO> getAuctionByID(String auctionID);

    Optional<AuctionDAO> putAuction(AuctionDAO auction);

    Optional<AuctionDAO> updateAuction(AuctionDAO auction);

    boolean delAuctionByID(String auctionID, String owner_nickname);

    Stream<BidDAO> getBidsByAuctionID(String auctionID);

    Stream<BidDAO> getTopBidsByAuctionID(String auctionID, Long n);

    Optional<BidDAO> putBid(BidDAO bid);

    Stream<QuestionDAO> getQuestionsByAuctionID(String auctionID);

    Stream<AuctionDAO> getAuctionsByUser(String nickname);

    Stream<AuctionDAO> getAuctionsClosingInXMins(long x);

    Optional<QuestionDAO> putQuestion(QuestionDAO question);

    Optional<QuestionDAO> updateQuestion(QuestionDAO question);

    Optional<QuestionDAO> getQuestionByID(String questionId);

    Optional<String> storeCookie(String key, String value);

    Optional<String> getCookie(String key);

    boolean deleteCookie(String key);
}

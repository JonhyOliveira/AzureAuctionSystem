package scc.data.layers.db;

import scc.data.models.AuctionDAO;
import scc.data.models.BidDAO;
import scc.data.models.QuestionDAO;

import java.util.Optional;
import java.util.stream.Stream;

public interface DBLayer {
    Stream<AuctionDAO> getClosingAuctions();

    Optional<AuctionDAO> updateAuction(AuctionDAO auction);

    Stream<AuctionDAO> getAuctionsByUser(String nickname);

    boolean delAuctionByID(String auctionID, String owner_nickname);

    Stream<String> getAllImagesFromTable(String table);

    Stream<BidDAO> getBidsByUser(String nickname);

    Stream<QuestionDAO> getQuestionsAskedByUser(String nickname);

    void updateBid(BidDAO bidDAO);

    void updateQuestion(QuestionDAO questionDAO);

    boolean deleteUserByNickname(String nickname);
}

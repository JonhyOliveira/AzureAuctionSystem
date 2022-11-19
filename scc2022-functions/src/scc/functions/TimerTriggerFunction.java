package scc.functions;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import scc.data.AuctionDAO;
import scc.data.UserDAO;
import scc.data.layers.RedisCacheLayer;
import scc.data.layers.CosmosDBLayer;

/**
 * Azure Functions with Timer trigger.
 * Schedule is: second minute hour ...
 * Apagar auctions
 * Apagar users da cache
 */
public class TimerTriggerFunction {

    /**
     * This function will be invoked every 2 minutes to ensure that the auctions are closed
     */
    @FunctionName("CloseAuctions")
    public void closeAuctions(
        @TimerTrigger(
                    name = "closeAuctionTrigger",
                    schedule = "0 */2 * * * *")
                    String timerInfo,
        final ExecutionContext context) {

        CosmosDBLayer db = CosmosDBLayer.getInstance();
        context.getLogger().info("Auction closure executed @ " + LocalTime.now());
        
        /*
         * Get all auctions that are expired and delete them
         */
        List<String> closedAuctionsIDs = db.getClosingAuctions().map(auctionDAO -> {
            auctionDAO.setClosed(true);
            db.updateAuction(auctionDAO);
            return auctionDAO.getAuctionID();
        }).collect(Collectors.toList());


        context.getLogger().info(String.format("Closed %d auctions: [ %s ]", closedAuctionsIDs.size(),
                String.join(", ", closedAuctionsIDs)));

    }

    /**
     * This funtion will be invoked every 10 minutes to ensure that auctions from deleted users are deleted
     */
    @FunctionName("GC-AuctionsNullOwner")
    public void deleteAuctionNullUser(
            @TimerTrigger(
                    name = "GC-NullAuctionOwnerTrigger",
                    schedule = "* */10 * * * *")
            String timerInfo,
            final ExecutionContext context) {
        context.getLogger().info("GC function executed @ " + LocalDateTime.now());
        CosmosDBLayer db = CosmosDBLayer.getInstance();

        List<String> deletedAuctions = db.getAuctionsByUser(null)
                .map(auctionDAO -> db.delAuctionByID(auctionDAO.getAuctionID(), auctionDAO.getOwnerNickname()) ? auctionDAO.getAuctionID() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        context.getLogger().info(String.format("Deleted %d auctions: [ %s ]", deletedAuctions.size(),
                String.join(", ", deletedAuctions)));
    }

}

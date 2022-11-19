package scc.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import scc.data.layers.CosmosDBLayer;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class CloseAuctionFunction {

    /**
     * This function will be invoked every 2 minutes to ensure that the auctions are closed
     */
    @FunctionName("CloseAuctions")
    public void run(
            @TimerTrigger(
                    name = "closeAuctionTrigger",
                    schedule = "0 */2 * * * *")
            String timerInfo,
            final ExecutionContext context) {

        CosmosDBLayer db = CosmosDBLayer.getInstance();
        context.getLogger().info("Auction closure executed @ " + LocalTime.now());

        // get auction that should be closing
        List<String> closedAuctionsIDs = db.getClosingAuctions().map(auctionDAO -> {
            // close and update the auction
            auctionDAO.setClosed(true);
            db.updateAuction(auctionDAO);
            // return the auction id for logging
            return auctionDAO.getAuctionID();
        }).collect(Collectors.toList());

        context.getLogger().info(String.format("Closed %d auctions: [ %s ]", closedAuctionsIDs.size(),
                String.join(", ", closedAuctionsIDs)));

    }

}

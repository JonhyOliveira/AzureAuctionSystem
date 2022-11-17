package scc.functions;

import java.time.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with Timer trigger.
 * Schedule is: second minute hour ...
 * Apagar auctions
 * Apagar users da cache
 */
public class TimerTriggerFunction {

    /**
     * This function will be invoked every 5min to ensure that the auction is deleted
     */
    @FunctionName("DeleteAuctionPeriodicFunction")
    public void deleteAuctionCleanup(
        @TimerTrigger(
                    name = "deleleAuctionPeriodicTrigger", 
                    schedule = "*/5 * * * *") 
                    String timerInfo,
        final ExecutionContext context) {
        
        CosmosDBLayer db = new CosmosDBLayer();
        RedisCacheLayer cache = new RedisCacheLayer();
        
        /*
         * Get all auctions that are expired and delete them
         */
        List<AuctionDAO> auctions = db.getClosedAuctions();
        for (AuctionDAO auction : auctions) {
            //db.deleteAuction(auction.getId()); ------------------------------ provavelmente não é para apagar da base de dados
            cache.deleteFromCache("auction:"+ auction.getId());
        }
    }

    /**
     * This function will be invoked every 5min to ensure that the user is deleted from cache
     */
    @FunctionName("DeleteUserPeriodicFunction")
    public void deleteUserCleanup(
        @TimerTrigger(
                    name = "deleleUserPeriodicTrigger", 
                    schedule = "*/5 * * * *") 
                    String timerInfo,
        final ExecutionContext context) {
        
        CosmosDBLayer db = new CosmosDBLayer();
        RedisCacheLayer cache = new RedisCacheLayer();
        
        /*
         * Get all users that are expired and delete them
         */
        List<UserDAO> users = cache.getExpiredUsers();
        for (UserDAO user : users) {
            cache.deleteFromCache("user:"+ user.getNickname());
        }
    }

}

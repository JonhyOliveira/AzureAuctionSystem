package scc.functions;

import com.azure.cosmos.implementation.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.CosmosDBTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import redis.clients.jedis.Jedis;
import scc.data.layers.RedisCacheLayer;
import scc.data.models.AuctionDAO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpdateRecentAuctionsOnUpdate {

    /*
     * Funtion for maintaining list of most recent auctions
     */
    @FunctionName("updateAuctionListTriggerFunction")
    public void updateMostRecentAuctions(
            @CosmosDBTrigger(
                    name = "updateMostRecentAuctionsCosmosDBTrigger",
                    databaseName = "scc-backend-database",
                    collectionName = "auctions",
                    connectionStringSetting = "CosmosDBConnection",
                    leaseCollectionName = "leases",
                    createLeaseCollectionIfNotExists = true
            ) String[] documents,
            final ExecutionContext context) {
        context.getLogger().info("Java CosmosDB trigger function executed.");

        Arrays.stream(documents).forEach(context.getLogger()::info);

        try(Jedis jedis = RedisCacheLayer.getCachePool().getResource()) {
            Arrays.stream(documents)
                    .filter(Objects::nonNull)
                    .map(document -> {
                        try {
                            return new ObjectMapper().readValue(document, AuctionDAO.class);
                        } catch (JsonProcessingException e) {
                            context.getLogger().warning(e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(auctionDAO -> !auctionDAO.isClosed())
                    .map(AuctionDAO::getAuctionID)
                    .forEach(s -> {
                        jedis.lpush("recentlyUpdatedAuctions", s);
                    });
            jedis.ltrim("recentlyUpdatedAuctions", 0, 20);
        }

    }

}

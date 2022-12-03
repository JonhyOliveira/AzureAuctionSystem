package scc.data.layers;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.PartitionKey;
import org.junit.jupiter.api.Test;
import scc.data.layers.db.CosmosDBLayer;

class CosmosDBLayerTest {


    @Test
    public void ola()
    {
        String nickname = "\" or 1=1";
        String query = "SELECT * FROM users WHERE users.id=\"" + nickname + "\"";

        System.out.println("With:");
        System.out.println(new PartitionKey("ola"));
        System.out.println("Without:\n" + query);
    }

    @Test
    void getAuctionsClosingInXMins() {
        CosmosDBLayer.getInstance().getAuctionsClosingInXMins(20).forEach(System.out::println);
    }

    @Test
    void resetContainers() {
        CosmosDatabase client = new CosmosClientBuilder()
                .endpoint(System.getenv("DB_URI"))
                .key(System.getenv("DB_PKEY"))
                // .directMode() // connects directly to backend node
                .gatewayMode() // one more hop (needed to work within FCT)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .connectionSharingAcrossClientsEnabled(true)
                .contentResponseOnWriteEnabled(true)
                .buildClient().getDatabase(System.getenv("DB_NAME"));

        client.getContainer("auctions").delete();
        client.getContainer("users").delete();
        client.getContainer("bids").delete();
        client.getContainer("questions").delete();

        client.createContainer("auctions", "/owner_nickname");
        client.createContainer("users", "/id");
        client.createContainer("bids", "/auction_id");
        client.createContainer("questions", "/auction_id");

        RedisCacheLayer.getCachePool().getResource().flushAll();
    }
}
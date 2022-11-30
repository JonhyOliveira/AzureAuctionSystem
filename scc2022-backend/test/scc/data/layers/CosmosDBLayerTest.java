package scc.data.layers;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.implementation.apachecommons.text.translate.AggregateTranslator;
import com.azure.cosmos.implementation.apachecommons.text.translate.UnicodeUnescaper;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosDatabaseRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.SqlQuerySpec;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
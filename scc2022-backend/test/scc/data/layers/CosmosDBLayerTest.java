package scc.data.layers;

import com.azure.cosmos.implementation.apachecommons.text.translate.AggregateTranslator;
import com.azure.cosmos.implementation.apachecommons.text.translate.UnicodeUnescaper;
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
}
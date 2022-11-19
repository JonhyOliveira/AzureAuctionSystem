package scc.data.layers;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.models.SearchOptions;
import scc.data.models.AuctionDAO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

public class CognitiveSearchLayer {

    private static final String AUCTIONS_INDEX = "";

    private static CognitiveSearchLayer instance;
    private SearchClient client = null;

    private SearchClient getClient() {
        if (Objects.isNull(client))
            client = new SearchClientBuilder()
                    .endpoint(System.getenv("SEARCH_ENDPOINT"))
                    .credential(new AzureKeyCredential(System.getenv("SEARCH_ADMIN_KEY")))
                    .indexName(AUCTIONS_INDEX)
                    .buildClient();
        return client;
    }

    public static CognitiveSearchLayer getInstance()
    {
        if (Objects.isNull(instance))
            instance = new CognitiveSearchLayer();
        return instance;
    }

    public Stream<AuctionDAO> findAuction(String queryText)
    {
        return getClient().search(queryText).stream().map(searchResult -> searchResult.getDocument(AuctionDAO.class));
    }

}

package scc.data.layers;

import com.azure.core.implementation.util.EnvironmentConfiguration;
import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import scc.data.AuctionDAO;

import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

public class CosmosDBLayer {

    private static CosmosDBLayer instance;

    public static synchronized CosmosDBLayer getInstance() {
        if( instance != null)
            return instance;

        CosmosClient client = new CosmosClientBuilder()
                .endpoint("https://scc-backend-db.documents.azure.com:443/")
                .key("NoxlgMjT9lxW2Bq6355IlKbQ1WJybDuLiz7sw22L9GLatWMIUxYQnFsU49vkCH5n58ewhDihNgcdrwdaUE4QQw==")
                // .directMode() // connects directly to backend node
                .gatewayMode() // one more hop (needed to work within FCT)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .connectionSharingAcrossClientsEnabled(true)
                .contentResponseOnWriteEnabled(true)
                .buildClient();
        instance = new CosmosDBLayer( client);
        return instance;

    }

    private final CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer users;
    private CosmosContainer auctions;
    private CosmosContainer bids;
    private CosmosContainer questions;
    private CosmosContainer cookies;

    public CosmosDBLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if( db != null)
            return;

        db = client.getDatabase("scc-backend-database");

        // containers
        users = db.getContainer("users");
        auctions = db.getContainer("auctions");
        bids = db.getContainer("bids");
        questions = db.getContainer("questions");
        cookies = db.getContainer("cookies");
    }

    public Stream<AuctionDAO> getClosingAuctions(){
        init();
        return auctions.queryItems("SELECT * FROM auctions WHERE auctions.end_time <= GetCurrentTimestamp() AND NOT auctions.closed",
                new CosmosQueryRequestOptions(), AuctionDAO.class).stream();
    }

    public Optional<AuctionDAO> updateAuction(AuctionDAO auction)
    {
        init();
        PartitionKey key = new PartitionKey(auction.getOwnerNickname());
        return Optional.ofNullable(auctions.replaceItem(auction, auction.getAuctionID(), key, new CosmosItemRequestOptions())
                .getItem());
    }

    public Stream<AuctionDAO> getAuctionsByUser(String nickname){
        init();
        return auctions.queryItems("SELECT * FROM auctions WHERE auctions.owner_nickname=\"" + nickname + "\"",
                new CosmosQueryRequestOptions(), AuctionDAO.class).stream();
    }

    public boolean delAuctionByID(String auctionID, String owner_nickname)
    {
        init();
        PartitionKey key = new PartitionKey(owner_nickname);
        int result = auctions.deleteItem(auctionID, key, new CosmosItemRequestOptions()).getStatusCode();
        return result >= 200 && result < 300;
    }

}

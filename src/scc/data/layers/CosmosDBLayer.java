package scc.data.layers;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import scc.data.models.AuctionDAO;
import scc.data.models.BidDAO;
import scc.data.models.QuestionDAO;
import scc.data.models.UserDAO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CosmosDBLayer {

	private static final String CONNECTION_URL, DB_KEY, DB_NAME;

	static // read from properties file
	{
		try {
			InputStream fis = CosmosDBLayer.class.getClassLoader().getResourceAsStream("database.properties");
			Properties props = new Properties();

			props.load(fis);

			CONNECTION_URL = props.getProperty("URI");
			DB_KEY = props.getProperty("DB_PKEY");
			DB_NAME = props.getProperty("DB_NAME");

			System.out.printf("CosmosDB = %s@%s\n", DB_NAME, CONNECTION_URL);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static CosmosDBLayer instance;

	public static synchronized CosmosDBLayer getInstance() {
		if( instance != null)
			return instance;

		CosmosClient client = new CosmosClientBuilder()
		         .endpoint(CONNECTION_URL)
		         .key(DB_KEY)
		         // .directMode() // connects directly to backend node
		         .gatewayMode() // one more hop (needed to work within FCT)
		         .consistencyLevel(ConsistencyLevel.SESSION)
		         .connectionSharingAcrossClientsEnabled(true)
		         .contentResponseOnWriteEnabled(true)
		         .buildClient();
			instance = new CosmosDBLayer( client);
		return instance;
		
	}
	
	private CosmosClient client;
	private CosmosDatabase db;
	private CosmosContainer users;
	private CosmosContainer auctions;
	private CosmosContainer bids;
	private CosmosContainer questions;

	
	public CosmosDBLayer(CosmosClient client) {
		this.client = client;
	}
	
	private synchronized void init() {
		if( db != null)
			return;

		db = client.getDatabase(DB_NAME);

		// containers
		users = db.getContainer("users");
		auctions = db.getContainer("auctions");
		bids = db.getContainer("bids");
		questions = db.getContainer("questions");
	}

	public CosmosItemResponse<UserDAO> updateUser(UserDAO newUser)
	{
		init();
		PartitionKey key = new PartitionKey(newUser.getId());
		return users.replaceItem(newUser, newUser.getId(), key, new CosmosItemRequestOptions());
	}

	public CosmosItemResponse<Object> delUserByNick(String nickname) {
		init();
		PartitionKey key = new PartitionKey( nickname);
		return users.deleteItem(nickname, key, new CosmosItemRequestOptions());
	}
	
	public CosmosItemResponse<Object> delUser(UserDAO user) {
		init();
		return users.deleteItem(user, new CosmosItemRequestOptions());
	}
	
	public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
		init();
		return users.createItem(user);
	}
	
	public CosmosPagedIterable<UserDAO> getUserByNick( String nickname) {
		init();
		return users.queryItems("SELECT * FROM users WHERE users.id=\"" + nickname + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
	}

	public CosmosPagedIterable<UserDAO> getUsers() {
		init();
		return users.queryItems("SELECT * FROM users ", new CosmosQueryRequestOptions(), UserDAO.class);
	}

	public CosmosPagedIterable<AuctionDAO> getAuctionByID(String auctionID){
		init();
		return auctions.queryItems("SELECT * FROM auctions WHERE auctions.id=\"" + auctionID + "\"", new CosmosQueryRequestOptions(), AuctionDAO.class);
	}
	public CosmosItemResponse<AuctionDAO> putAuction(AuctionDAO auction){
		init();
		return auctions.createItem(auction);
	}

	public CosmosItemResponse<AuctionDAO> updateAuction(AuctionDAO auction)
	{
		init();
		PartitionKey key = new PartitionKey(auction.auctionID());
		return users.replaceItem(auction, auction.auctionID(), key, new CosmosItemRequestOptions());
	}

	public CosmosItemResponse<Object> delAuctionByID(String auctionID)
	{
		init();
		PartitionKey key = new PartitionKey(auctionID);
		return auctions.deleteItem(auctionID, key, new CosmosItemRequestOptions());
	}

	public CosmosPagedIterable<BidDAO> getBidsByAuctionID(String auctionID)
	{
		init();
		return bids.queryItems("SELECT * FROM bids WHERE bids.auction_id=\"" + auctionID + "\"",
				new CosmosQueryRequestOptions(), BidDAO.class);
	}

	public CosmosPagedIterable<BidDAO> getTopBidsByAuctionID(String auctionID, Long n)
	{
		init();
		return bids.queryItems("SELECT TOP " + n + " * FROM bids WHERE bids.auction_id=\"" + auctionID + "\" ORDER BY amount",
				new CosmosQueryRequestOptions(), BidDAO.class);
	}

	public CosmosItemResponse<BidDAO> putBid(BidDAO bid) {
		init();
		return bids.createItem(bid);
	}

	public CosmosPagedIterable<QuestionDAO> getQuestionsByAuctionID(String auctionID){
		init();
		return questions.queryItems("SELECT * FROM questions WHERE questions.auctionID=\"" + auctionID + "\"", new CosmosQueryRequestOptions(), QuestionDAO.class);
	}

	public void close() {
		client.close();
	}

}

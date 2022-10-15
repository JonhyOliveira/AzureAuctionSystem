package scc.data.layers;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import scc.data.models.AuctionDAO;
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

	
	public CosmosDBLayer(CosmosClient client) {
		this.client = client;
	}
	
	private synchronized void init() {
		if( db != null)
			return;
		System.out.println("Initing...");
		db = client.getDatabase(DB_NAME);
		System.out.println("Got database.");
		users = db.getContainer("users");
		System.out.println("Got users table.");
		auctions = db.getContainer("auctions");
		System.out.println("Got auctions table.");
	}

	public CosmosItemResponse<UserDAO> updateUser(String nickname, UserDAO newUser)
	{
		init();
		PartitionKey key = new PartitionKey(nickname);
		return users.replaceItem(newUser, nickname, key, new CosmosItemRequestOptions());
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

	public CosmosPagedIterable<AuctionDAO> getAuctionByTitle(String title){
		init();
		return auctions.queryItems("SELECT * FROM auctions WHERE auctions.title=\"" + title + "\"", new CosmosQueryRequestOptions(), AuctionDAO.class);
	}
	public CosmosItemResponse<AuctionDAO> putAuction(AuctionDAO auction){
		init();
		return auctions.createItem(auction);
	}

	public void close() {
		client.close();
	}
	
	
}

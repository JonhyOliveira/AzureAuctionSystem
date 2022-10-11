package scc.database;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

			System.out.println(CONNECTION_URL);
			System.out.println(DB_KEY);
			System.out.println(DB_NAME);

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
	}

	public CosmosItemResponse<Object> delUserById(String id) {
		init();
		PartitionKey key = new PartitionKey( id);
		return users.deleteItem(id, key, new CosmosItemRequestOptions());
	}
	
	public CosmosItemResponse<Object> delUser(UserDAO user) {
		init();
		return users.deleteItem(user, new CosmosItemRequestOptions());
	}
	
	public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
		init();
		return users.createItem(user);
	}
	
	public CosmosPagedIterable<UserDAO> getUserById( String id) {
		init();
		return users.queryItems("SELECT * FROM users WHERE users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
	}

	public CosmosPagedIterable<UserDAO> getUsers() {
		init();
		return users.queryItems("SELECT * FROM users ", new CosmosQueryRequestOptions(), UserDAO.class);
	}

	public void close() {
		client.close();
	}
	
	
}

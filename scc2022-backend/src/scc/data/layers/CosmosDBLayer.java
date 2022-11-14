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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

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
	
	private final CosmosClient client;
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

	// ! FUNCTION TO VERIFY LOGIN CREDENTIALS, dont know if its the best way to do it
	public boolean verifyLogin(String nickname, String pwd) {
		init();
		PartitionKey key = new PartitionKey(nickname);
		UserDAO user = users.readItem(nickname, key, UserDAO.class).getItem();

		return (user != null && user.getPwd().equals(Hash.of(pwd)));

	}

	public CosmosItemResponse<UserDAO> updateUser(UserDAO newUser)
	{
		init();
		PartitionKey key = new PartitionKey(newUser.getNickname());
		return users.replaceItem(newUser, newUser.getNickname(), key, new CosmosItemRequestOptions());
	}

	public CosmosItemResponse<Object> delUserByNick(String nickname) {
		init();
		PartitionKey key = new PartitionKey( nickname);
		return users.deleteItem(nickname, key, new CosmosItemRequestOptions());
	}
	
	@SuppressWarnings("unused")
	public CosmosItemResponse<Object> delUser(UserDAO user) {
		init();
		return users.deleteItem(user, new CosmosItemRequestOptions());
	}
	
	public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
		init();
		return users.createItem(user);
	}
	
	public Optional<UserDAO> getUserByNick(String nickname) {
		init();
		SqlQuerySpec query = new SqlQuerySpec("SELECT * FROM users WHERE users.id=@nickname",
				List.of(new SqlParameter("@nickname", nickname)));

		return users.queryItems(query, new CosmosQueryRequestOptions(), UserDAO.class)
				.stream().findAny();
	}

	@SuppressWarnings("unused")
	public Iterator<UserDAO> getUsers() {
		init();
		return users.queryItems("SELECT * FROM users ", new CosmosQueryRequestOptions(), UserDAO.class)
				.stream().iterator();
	}

	public Optional<AuctionDAO> getAuctionByID(String auctionID){
		init();
		return auctions.queryItems("SELECT * FROM auctions WHERE auctions.id=\"" + auctionID + "\"",
						new CosmosQueryRequestOptions(), AuctionDAO.class).stream().findAny();
	}
	public Optional<AuctionDAO> putAuction(AuctionDAO auction){
		init();
		return Optional.ofNullable(auctions.createItem(auction).getItem());
	}

	public Optional<AuctionDAO> updateAuction(AuctionDAO auction)
	{
		init();
		PartitionKey key = new PartitionKey(auction.getOwnerNickname());
		return Optional.ofNullable(auctions.replaceItem(auction, auction.getAuctionID(), key, new CosmosItemRequestOptions())
				.getItem());
	}

	public boolean delAuctionByID(String auctionID, String owner_nickname)
	{
		init();
		PartitionKey key = new PartitionKey(owner_nickname);
		int result = auctions.deleteItem(auctionID, key, new CosmosItemRequestOptions()).getStatusCode();
		return result >= 200 && result < 300;
	}

	public Stream<BidDAO> getBidsByAuctionID(String auctionID)
	{
		init();
		return bids.queryItems("SELECT * FROM bids WHERE bids.auction_id=\"" + auctionID + "\"",
				new CosmosQueryRequestOptions(), BidDAO.class).stream();
	}

	public Stream<BidDAO> getTopBidsByAuctionID(String auctionID, Long n)
	{
		init();
		return bids.queryItems("SELECT TOP " + n + " * FROM bids WHERE bids.auction_id=\"" + auctionID + "\" ORDER BY amount",
				new CosmosQueryRequestOptions(), BidDAO.class).stream();
	}

	public Optional<BidDAO> putBid(BidDAO bid) {
		init();
		return Optional.ofNullable(bids.createItem(bid).getItem());
	}

	public Stream<QuestionDAO> getQuestionsByAuctionID(String auctionID){
		init();
		return questions.queryItems("SELECT * FROM questions WHERE questions.auction_id=\"" + auctionID + "\"",
						new CosmosQueryRequestOptions(), QuestionDAO.class).stream();
	}

	public Stream<AuctionDAO> getAuctionsByUser(String nickname){
		init();
		return auctions.queryItems("SELECT * FROM auctions WHERE auctions.owner_nickname=\"" + nickname + "\"",
				new CosmosQueryRequestOptions(), AuctionDAO.class).stream();
	}

	/*
	public CosmosPagedIterable<AuctionDAO> getClosingAuctions(){
		init();
		return auctions.queryItems("SELECT * FROM auctions WHERE ((auctions.endTime - MINUTE(now())) <= 5)\"", new CosmosQueryRequestOptions(), AuctionDAO.class);
	}
	*/

	public Stream<AuctionDAO> getClosingAuctions(){
		init();
		return auctions.queryItems("SELECT * FROM auctions WHERE auctions.end_time <= GetCurrentTimestamp() AND NOT auctions.closed",
				new CosmosQueryRequestOptions(), AuctionDAO.class).stream();
	}
	
	@SuppressWarnings("unused")
	public void close() {
		client.close();
	}

	public Optional<QuestionDAO> putQuestion(QuestionDAO question)
	{
		init();
		return Optional.ofNullable(questions.createItem(question).getItem());
	}

	public Optional<QuestionDAO> updateQuestion(QuestionDAO question)
	{
		init();
		PartitionKey key = new PartitionKey(question.getAuctionID());
		return Optional.ofNullable(questions.replaceItem(question, question.getId(), key, new CosmosItemRequestOptions()).getItem());
	}

	public Optional<QuestionDAO> getQuestionByID(String questionId) {
		init();
		return questions.queryItems("SELECT * FROM questions WHERE questions.id=\"" + questionId + "\"",
				new CosmosQueryRequestOptions(), QuestionDAO.class).stream().findFirst();
	}
}

package scc.data.layers.db;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import scc.data.models.*;
import scc.session.Session;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CosmosDBLayer implements DBLayer {

	private static CosmosDBLayer instance;

	public static synchronized CosmosDBLayer getInstance() {
		if( instance != null)
			return instance;

		CosmosClient client = new CosmosClientBuilder()
				.endpoint(System.getenv("DB_URI"))
				.key(System.getenv("DB_PKEY"))
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

		db = client.getDatabase(System.getenv("DB_NAME"));

		// containers
		users = db.getContainer("users");
		auctions = db.getContainer("auctions");
		bids = db.getContainer("bids");
		questions = db.getContainer("questions");
		cookies = db.getContainer("cookies");
	}

	@Override
	public Optional<UserDAO> updateUser(UserDAO newUser)
	{
		init();
		CosmosItemResponse<UserDAO> res = users.upsertItem(newUser);
		if (res.getStatusCode() >= 200 && res.getStatusCode() < 300)
			return Optional.of(res.getItem());
		else
			return Optional.empty();
	}

	@Override
	public boolean delUserByNick(String nickname) {
		init();
		PartitionKey key = new PartitionKey( nickname);
		int result = users.deleteItem(nickname, key, new CosmosItemRequestOptions()).getStatusCode();
		return result >= 200 && result < 300;
	}
	
	@Override
	@SuppressWarnings("unused")
	public boolean delUser(UserDAO user) {
		init();
		int result = users.deleteItem(user, new CosmosItemRequestOptions()).getStatusCode();
		return result >= 200 && result < 300;
	}
	
	@Override
	public Optional<UserDAO> putUser(UserDAO user) {
		init();
		return Optional.of(users.createItem(user))
				.filter(response -> response.getStatusCode() >= 200 && response.getStatusCode() < 300)
				.map(CosmosItemResponse::getItem);
	}
	
	@Override
	public Optional<UserDAO> getUserByNick(String nickname) {
		init();
		SqlQuerySpec query = new SqlQuerySpec("SELECT * FROM users WHERE users.id=@nickname",
				List.of(new SqlParameter("@nickname", nickname)));

		return users.queryItems(query, new CosmosQueryRequestOptions(), UserDAO.class)
				.stream().findAny();
	}

	@Override
	@SuppressWarnings("unused")
	public Stream<UserDAO> getUsers() {
		init();
		return users.queryItems("SELECT * FROM users", new CosmosQueryRequestOptions(), UserDAO.class)
				.stream();
	}

	@Override
	public Optional<AuctionDAO> getAuctionByID(String auctionID){
		init();
		return auctions.queryItems("SELECT * FROM auctions WHERE auctions.id=\"" + auctionID + "\"",
						new CosmosQueryRequestOptions(), AuctionDAO.class).stream().findAny();
	}
	@Override
	public Optional<AuctionDAO> putAuction(AuctionDAO auction){
		init();
		return Optional.ofNullable(auctions.createItem(auction).getItem());
	}

	@Override
	public Optional<AuctionDAO> updateAuction(AuctionDAO auction)
	{
		init();
		PartitionKey key = new PartitionKey(auction.getOwnerNickname());
		return Optional.ofNullable(auctions.replaceItem(auction, auction.getAuctionID(), key, new CosmosItemRequestOptions())
				.getItem());
	}

	@Override
	public boolean delAuctionByID(String auctionID, String owner_nickname)
	{
		init();
		PartitionKey key = new PartitionKey(owner_nickname);
		int result = auctions.deleteItem(auctionID, key, new CosmosItemRequestOptions()).getStatusCode();
		return result >= 200 && result < 300;
	}

	@Override
	public Stream<BidDAO> getBidsByAuctionID(String auctionID)
	{
		init();
		return bids.queryItems("SELECT * FROM bids WHERE bids.auction_id=\"" + auctionID + "\"",
				new CosmosQueryRequestOptions(), BidDAO.class).stream();
	}

	@Override
	public Stream<BidDAO> getTopBidsByAuctionID(String auctionID, Long n)
	{
		init();
		return bids.queryItems("SELECT TOP " + n + " * FROM bids WHERE bids.auction_id=\"" + auctionID + "\" ORDER BY bids.amount DESC",
				new CosmosQueryRequestOptions(), BidDAO.class).stream();
	}

	@Override
	public Optional<BidDAO> putBid(BidDAO bid) {
		init();
		return Optional.ofNullable(bids.createItem(bid).getItem());
	}

	@Override
	public Stream<QuestionDAO> getQuestionsByAuctionID(String auctionID){
		init();
		return questions.queryItems("SELECT * FROM questions WHERE questions.auction_id=\"" + auctionID + "\"",
						new CosmosQueryRequestOptions(), QuestionDAO.class).stream();
	}

	@Override
	public Stream<AuctionDAO> getAuctionsByUser(String nickname){
		init();
		return auctions.queryItems("SELECT * FROM auctions WHERE auctions.owner_nickname=\"" + nickname + "\"",
				new CosmosQueryRequestOptions(), AuctionDAO.class).stream();
	}

	@Override
	public Stream<AuctionDAO> getAuctionsClosingInXMins(long x){
		init();
		return auctions.queryItems("SELECT * FROM auctions WHERE auctions.end_time <= (GetCurrentTimestamp() + " + x + " * 60000) AND NOT auctions.closed",
				new CosmosQueryRequestOptions(), AuctionDAO.class).stream();
	}
	
	@SuppressWarnings("unused")
	public void close() {
		client.close();
	}

	@Override
	public Optional<QuestionDAO> putQuestion(QuestionDAO question)
	{
		init();
		return Optional.ofNullable(questions.createItem(question).getItem());
	}

	@Override
	public Optional<QuestionDAO> updateQuestion(QuestionDAO question)
	{
		init();
		PartitionKey key = new PartitionKey(question.getAuctionID());
		return Optional.ofNullable(questions.replaceItem(question, question.getId(), key, new CosmosItemRequestOptions()).getItem());
	}

	@Override
	public Optional<QuestionDAO> getQuestionByID(String questionId) {
		init();
		return questions.queryItems("SELECT * FROM questions WHERE questions.id=\"" + questionId + "\"",
				new CosmosQueryRequestOptions(), QuestionDAO.class).stream().findFirst();
	}

	@Override
	public Optional<String> storeCookie(String key, String value) {
		init();
		return Optional.of(cookies.upsertItem(new CookieDAO(key, value, Session.VALIDITY_SECONDS)))
				.filter(res -> res.getStatusCode() >= 200 && res.getStatusCode() < 300)
				.map(CosmosItemResponse::getItem)
				.map(CookieDAO::getValue);


	}

	@Override
	public Optional<String> getCookie(String key) {
		init();
		return cookies.queryItems("SELECT * FROM cookies WHERE cookies.id=\"" + key + "\"",
				new CosmosQueryRequestOptions(), CookieDAO.class)
				.stream()
				.findAny()
				.map(CookieDAO::getValue);
	}

	@Override
	public boolean deleteCookie(String key) {
		init();
		return getCookie(key)
				.map(s -> cookies.deleteItem(s, new PartitionKey(s), new CosmosItemRequestOptions()))
				.filter(response -> response.getStatusCode() >= 200 && response.getStatusCode() < 300)
				.isPresent();
	}

}

package scc.srv;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import scc.data.Auction;
import scc.data.Bid;
import scc.data.Question;
import scc.data.User;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AuctionResourceTest {

    static AuctionResource resource;
    String id;

    @org.junit.jupiter.api.BeforeAll
    static void setUpping()
    {
        resource = new AuctionResource();
        UserResourceTest.setUpping();
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        id = Long.toString(System.currentTimeMillis());
    }

    @Test
    void create() {
        User auction_owner = UserResourceTest.createRandomUser();

        Auction auction = new Auction(null, "Ford Focus 2016", "In great condition",
                "0:"+id, auction_owner.getNickname(), new Date().toInstant().plusSeconds(160L).toEpochMilli(),
                100, false);

        assertThrows(NotAuthorizedException.class, () -> resource.create(auction.copy(), auction_owner.getPwd() + "x"));

        assertThrows(BadRequestException.class, () ->
        {
            Auction details = auction.copy();
            details.setOwnerNickname("p");
            resource.create(details, auction_owner.getPwd());
        });

        System.out.println(auction);
        Auction created = resource.create(auction.copy(), auction_owner.getPwd());

        assertNotNull(created.getAuctionID());
    }

    @Test
    void update() {
        User auction_owner = UserResourceTest.createRandomUser();

        Auction auction = resource.create(new Auction(null, "Ford Focus 2016", "In great condition",
                "0:"+id, auction_owner.getNickname(), new Date().toInstant().plusSeconds(160L).toEpochMilli(),
                100, false).copy(), auction_owner.getPwd());

        System.out.println(auction);

        assertThrows(NotAuthorizedException.class, () -> { // wrong owner pwd
           Auction auc = auction.copy();
           resource.update(auc.copy(), auc.getAuctionID(), auction_owner.getPwd() + "x");
        });


        // close the auction
        assertDoesNotThrow(() -> {
            Auction auc = auction.copy();

            auc.setTitle("Ola ola");
            auc.setDescription("Testando");
            auc.setEndTime(System.currentTimeMillis() + 100000);
            auc.setIsClosed(true); // close auction

            Auction aucGot = resource.update(auc.copy(), auc.getAuctionID(), auction_owner.getPwd());
            Auction aucExpected = auction.copy().patch(auc).orElse(null);

            assertEquals(aucExpected, aucGot);
        });

        // auction should be closed to changes
        assertThrows(NotAuthorizedException.class, () -> {
            Auction auc = auction.copy();
            auc.setThumbnailId("ola");

            resource.update(auc.copy(), auc.getAuctionID(), auction_owner.getPwd() + "x");
        });
    }

    @Test
    void listBids() {
        User u = UserResourceTest.createRandomUser();

        Auction auc = createRandomAuction(u);

        System.out.println(resource.listBids(auc.getAuctionID()));
        assertArrayEquals(resource.listBids(auc.getAuctionID()).toArray(), new Object[0]);

        List<Bid> bids = Stream.of(1, 2, 3, 5, 6, 7)
                .map(integer -> createRandomBid(auc, UserResourceTest.createRandomUser())).toList();

        assertArrayEquals(resource.listBids(auc.getAuctionID()).toArray(), bids.toArray());
    }

    @Test
    void doBid() {
        throw new NotImplementedException();
    }

    @Test
    void listQuestions() {
        User u = UserResourceTest.createRandomUser();

        Auction auc = createRandomAuction(u);

        assertArrayEquals(resource.listQuestions(auc.getAuctionID()).toArray(), new Object[0]);

        List<Question> questions = Stream.of(1, 2, 3, 4, 5)
            .map(integer -> createRandomQuestion(auc, u)).toList();

        assertArrayEquals(questions.toArray(), resource.listQuestions(auc.getAuctionID()).toArray());
    }

    @Test
    void submitQuestion() {

    }

    @Test
    void submitReply() {
        throw new NotImplementedException();
    }

    @Test
    void showUserAuctions() {
        User u1 = UserResourceTest.createRandomUser(), u2 = UserResourceTest.createRandomUser();

        List<Auction> u1Aucs = Stream.of(1, 2, 3, 4, 5, 6).map(integer -> createRandomAuction(u1)).toList();
        List<Auction> u2Aucs = Stream.of(1, 2, 3).map(integer -> createRandomAuction(u2)).toList();

        assertArrayEquals(u1Aucs.toArray(), resource.showUserAuctions(u1.getNickname()).toArray(), "oh no!");
        assertArrayEquals(u2Aucs.toArray(), resource.showUserAuctions(u2.getNickname()).toArray(), "oh no!");
    }

    @Test
    void showAuctionsAboutToClose() {
        throw new NotImplementedException();
    }

    public static Question createRandomQuestion(Auction auction, User auction_owner)
    {
        User questioner = UserResourceTest.createRandomUser();


        Question created = resource.submitQuestion(new Question(null, questioner.getNickname(),
                "meaning of the universe, life and everything?", "42."), auction.getAuctionID(), questioner.getPwd());

        if (new Random().nextGaussian() < 0.5) {
            created = resource.submitReply(new Question(created.getQuestionID(), null, null, "42, GOD DAMNIT!"),
                    auction.getAuctionID(), auction_owner.getPwd());
        }
        System.out.println(created);
        return created;
    }

    public static Bid createRandomBid(Auction auction, User bidder)
    {
        Bid rand = new Bid(bidder.getNickname(), new Random().nextDouble(10000));

        resource.doBid(rand, auction.getAuctionID(), bidder.getPwd());

        return rand;
    }

    public static Auction createRandomAuction(User owner)
    {
        return resource.create(random(owner), owner.getPwd());
    }

    public static Auction random(User owner)
    {
        long id = System.currentTimeMillis();

        return new Auction(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), "0:" + id,
                owner.getNickname(), id + 1000000, 100.0f, false);
    }
}
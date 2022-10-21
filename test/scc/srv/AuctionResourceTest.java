package scc.srv;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import scc.data.Auction;
import scc.data.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AuctionResourceTest {

    static AuctionResource resource;
    static UserResource userResource;
    String id;

    @org.junit.jupiter.api.BeforeAll
    static void setUpping()
    {
        resource = new AuctionResource();
        userResource = new UserResource();
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        id = Long.toString(System.currentTimeMillis());
    }

    @Test
    void create() {
        User auction_owner = new User("j:" + id, "JJJ", "123", "0:"+id);

        userResource.create(auction_owner.copy());

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

        assertNotNull(created.auctionID());
    }

    @Test
    void update() {
        User auction_owner = new User("j:" + id, "JJJ", "123", "0:"+id);

        userResource.create(auction_owner.copy());

        Auction auction = resource.create(new Auction(null, "Ford Focus 2016", "In great condition",
                "0:"+id, auction_owner.getNickname(), new Date().toInstant().plusSeconds(160L).toEpochMilli(),
                100, false).copy(), auction_owner.getPwd());

        System.out.println(auction);

        assertThrows(NotAuthorizedException.class, () -> { // wrong owner pwd
           Auction auc = auction.copy();
           resource.update(auc.copy(), auc.auctionID(), auction_owner.getPwd() + "x");
        });


        // close the auction
        assertDoesNotThrow(() -> {
            Auction auc = auction.copy();

            auc.setTitle("Ola ola");
            auc.setDescription("Testando");
            auc.setEndTime(System.currentTimeMillis() + 100000);
            auc.setIsClosed(true); // close auction

            Auction aucGot = resource.update(auc.copy(), auc.auctionID(), auction_owner.getPwd());
            Auction aucExpected = auction.copy().patch(auc).orElse(null);

            assertEquals(aucExpected, aucGot);
        });

        // auction should be closed to changes
        assertThrows(NotAuthorizedException.class, () -> {
            Auction auc = auction.copy();
            auc.setThumbnailId("ola");

            resource.update(auc.copy(), auc.auctionID(), auction_owner.getPwd() + "x");
        });
    }

    @Test
    void listBids() {
        throw new NotImplementedException();
    }

    @Test
    void doBid() {
        throw new NotImplementedException();
    }

    @Test
    void listQuestions() {
        throw new NotImplementedException();
    }

    @Test
    void submitQuestion() {
        throw new NotImplementedException();
    }

    @Test
    void submitReply() {
        throw new NotImplementedException();
    }

    @Test
    void showUserAuctions() {
        User u1 = UserResourceTest.random();
    }

    @Test
    void showAuctionsAboutToClose() {
        throw new NotImplementedException();
    }
}
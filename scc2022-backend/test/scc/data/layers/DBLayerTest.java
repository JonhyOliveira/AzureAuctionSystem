package scc.data.layers;

import com.mongodb.MongoWriteException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import scc.data.layers.db.DBLayer;
import scc.data.layers.db.MongoDBLayer;
import scc.data.models.AuctionDAO;
import scc.data.models.UserDAO;

import java.time.*;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class DBLayerTest {

    DBLayer dbLayer; // = MongoDBLayer.getInstance("scc2022-project-db", "mongodb://root:1234@localhost:27017/");

    @Test
    void updateUser() {
        UserDAO initialUser = new UserDAO("pedro:" + LocalDateTime.now(), "Pedrocas", "1234", "4242"),
                newUser = new UserDAO(initialUser.getNickname(), "Pedro", "5555", "1234");

        dbLayer.putUser(initialUser);
        assertEquals(newUser, dbLayer.updateUser(newUser).orElse(null));
    }

    @Test
    void delUserByNick() {
        UserDAO userDAO = new UserDAO("leo:" + LocalDateTime.now(), "Leonardo", "XXXX", "PPP");

        dbLayer.putUser(userDAO);


    }

    @Test
    void delUser() {
        UserDAO user = new UserDAO();
        user.setNickname("pedro");

        assertTrue(dbLayer.delUser(user));
        assertTrue(dbLayer.getUserByNick(user.getNickname()).isEmpty());

    }

    @Test
    void putUser() {
        UserDAO userDAO = new UserDAO("jonhy", "João Oliveira", "1234", "X:247");

        // create user if it does not exist, else make sure an exception is thrown
        dbLayer.getUserByNick(userDAO.getNickname()).ifPresentOrElse(
                dao -> {
                    Logger.getLogger(this.getClass().getName()).info("Found " + dao);
                    assertThrows(MongoWriteException.class, () -> dbLayer.putUser(userDAO));
                    },
                () -> assertEquals(userDAO, dbLayer.putUser(userDAO).orElse(null))
        );

        assertTrue(dbLayer.getUserByNick(userDAO.getNickname()).isPresent());
    }

    @Test
    void getUserByNick() {
        dbLayer.getUsers().findAny().ifPresent(
                userDAO -> assertTrue(dbLayer.getUserByNick(userDAO.getNickname()).isPresent()));
    }

    @Test
    void getUsers() {
        dbLayer.getUsers().forEach(System.out::println);
    }

    @Test
    void getAuctionByID() {
    }

    @Test
    void putAuction() {
        dbLayer.getUsers().findAny().ifPresentOrElse(
                userDAO -> {
                    AuctionDAO auctionDAO = new AuctionDAO(UUID.randomUUID().toString(), "Teste",
                            "teste auction", "X:001", userDAO.getNickname(),
                            System.currentTimeMillis() + 2*24*60*60*1000, (float) Math.random() * 100,
                            false);
                    var res = dbLayer.putAuction(auctionDAO);
                    assertTrue(res.isPresent());
                    assertEquals(auctionDAO, res.get());
                },
                () -> {throw new NotFoundException("There are no users to register the auction under.");});
    }

    @Test
    void updateAuction() {
        if (dbLayer.getUsers().noneMatch( // checks if it was possible to find an auction to update
                userDAO -> {
                    AuctionDAO toUpdate = dbLayer.getAuctionsByUser(userDAO.getNickname())
                            .findAny()
                            .orElse(null);
                    if (toUpdate == null || toUpdate.isClosed())
                        return false;

                    toUpdate.setDescription("ola ola");
                    toUpdate.setClosed(true);
                    var res = dbLayer.updateAuction(toUpdate);
                    assertTrue(res.isPresent());
                    assertEquals(toUpdate, res.get());

                    return true;
                }))
            throw new NotFoundException("No valid auction found.");

    }

    @Test
    void delAuctionByID() {
        if (dbLayer.getUsers().noneMatch(
                userDAO -> {
                    AuctionDAO auction = dbLayer.getAuctionsByUser(userDAO.getNickname())
                            .findAny()
                            .orElse(null);
                    if (auction == null)
                        return false;

                    var res = dbLayer.getAuctionByID(auction.getAuctionID());

                    // check if exists
                    assertTrue(res.isPresent());
                    assertTrue(dbLayer.getAuctionsByUser(userDAO.getNickname()).anyMatch(auction::equals));

                    // make sure the return makes sense
                    assertTrue(dbLayer.delAuctionByID(auction.getAuctionID(), userDAO.getNickname()));

                    // check if it was really deleted
                    res = dbLayer.getAuctionByID(auction.getAuctionID());
                    assertTrue(res.isEmpty());
                    assertFalse(dbLayer.getAuctionsByUser(userDAO.getNickname()).anyMatch(auction::equals));

                    // make sure the return makes sense
                    assertTrue(dbLayer.delAuctionByID(auction.getAuctionID(), userDAO.getNickname()));

                    return true;
                }))
            throw new NotFoundException("No valid auction found.");
    }

    @Test
    void getBidsByAuctionID() {
        if (dbLayer.getUsers().noneMatch(
                userDAO -> {
                    AuctionDAO auction = dbLayer.getAuctionsByUser(userDAO.getNickname())
                            .findAny()
                            .orElse(null);
                    if (auction == null || auction.isClosed())
                        return false;

                    return true;
                }))
            throw new NotFoundException("No valid auction found.");
    }

    @Test
    void getTopBidsByAuctionID() {
        if (dbLayer.getUsers().noneMatch(
                userDAO -> {
                    AuctionDAO auction = dbLayer.getAuctionsByUser(userDAO.getNickname())
                            .findAny()
                            .orElse(null);
                    if (auction == null)
                        return false;

                    return true;
                }))
            throw new NotFoundException("No valid auction found.");
    }

    @Test
    void putBid() {
        if (dbLayer.getUsers().noneMatch(
                userDAO -> {
                    AuctionDAO auction = dbLayer.getAuctionsByUser(userDAO.getNickname())
                            .findAny()
                            .orElse(null);
                    if (auction == null || auction.isClosed())
                        return false;

                    return true;
                }))
            throw new NotFoundException("No valid auction found.");

    }

    @Test
    void getQuestionsByAuctionID() {
        if (dbLayer.getUsers().noneMatch(
                userDAO -> {
                    AuctionDAO auction = dbLayer.getAuctionsByUser(userDAO.getNickname())
                            .findAny()
                            .orElse(null);
                    if (auction == null)
                        return false;

                    return true;
                }))
            throw new NotFoundException("No valid auction found.");
    }

    @Test
    void getAuctionsByUser() {
        assertTrue(dbLayer.getUsers().anyMatch(userDAO -> dbLayer.getAuctionsByUser(userDAO.getNickname()).findAny().isPresent()));
    }

    @Test
    void getAuctionsClosingInXMins() {
    }

    @Test
    void putQuestion() {
    }

    @Test
    void updateQuestion() {
    }

    @Test
    void getQuestionByID() {
    }

    @Test
    void storeCookie() {
    }

    @Test
    void getCookie() {
    }

    @Test
    void deleteCookie() {
    }
}
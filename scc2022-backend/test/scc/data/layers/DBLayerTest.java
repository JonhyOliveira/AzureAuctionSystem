package scc.data.layers;

import com.mongodb.MongoWriteException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import scc.data.models.UserDAO;

import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class DBLayerTest {

    DBLayer dbLayer = MongoDBLayer.getInstance();

    @Test
    void updateUser() {
    }

    @Test
    void delUserByNick() {
    }

    @Test
    void delUser() {
        UserDAO user = new UserDAO();
        user.setNickname("pedro");
        assertTrue(dbLayer.delUser(user).isEmpty());
    }

    @Test
    void putUser() {
        UserDAO userDAO = new UserDAO("jonhy", "João Oliveira", "1234", "X:247");

        dbLayer.getUserByNick(userDAO.getNickname()).ifPresentOrElse(
                dao -> {
                    Logger.getLogger(this.getClass().getName()).info("Found " + dao);
                    assertThrows(MongoWriteException.class, () -> dbLayer.putUser(userDAO));
                    },
                () -> assertEquals(userDAO, dbLayer.putUser(userDAO))
        );
    }

    @Test
    void getUserByNick() {
    }

    @Test
    void getUsers() {
    }

    @Test
    void getAuctionByID() {
    }

    @Test
    void putAuction() {
    }

    @Test
    void updateAuction() {
    }

    @Test
    void delAuctionByID() {
    }

    @Test
    void getBidsByAuctionID() {
    }

    @Test
    void getTopBidsByAuctionID() {
    }

    @Test
    void putBid() {
    }

    @Test
    void getQuestionsByAuctionID() {
    }

    @Test
    void getAuctionsByUser() {
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
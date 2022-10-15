package scc.srv;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.function.Executable;
import scc.data.User;
import scc.data.models.UserDAO;

import static org.junit.jupiter.api.Assertions.*;

class UserResourceTest {

    static UserResource resource;
    String id;

    @org.junit.jupiter.api.BeforeAll
    static void setUpping()
    {
        resource = new UserResource();
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        id = Long.toString(System.currentTimeMillis());
    }

    @org.junit.jupiter.api.Test
    void create() {
        User u = new User("joao_0" + id, "Joao Oliveira", id, "0:" + id);
        User uCreated = resource.create(u);

        assertEquals(u.censored(), uCreated.censored());

        assertThrows(ForbiddenException.class, () -> {
            resource.create(u);
        });
    }

    @org.junit.jupiter.api.Test
    void delete() {
        User u = new User("joao_1" + id, "Joao Pedro", id, "1:" + id);

        Executable deleteUser = () -> {
            resource.delete(u.nickname(), u.pwd());
        };

        assertThrows(NotFoundException.class, deleteUser);

        resource.create(new UserDAO(u).toUser());

        assertDoesNotThrow(deleteUser);
    }

    @org.junit.jupiter.api.Test
    void update() {
        User u = new User("joao_2" + id, "Joao Pedro", id, "2:" + id);
        String id2 = Long.toString(System.currentTimeMillis());
        User u2 = new User("joao_2" + id2, "Joao Pedor", id2, "2:" + id2);

        resource.create(new UserDAO(u).toUser());
        User uGot = resource.update(u.nickname(), id, u2);
        User uExpected = u.patch(u2);

        assertEquals(uExpected.censored(), uGot.censored());
    }

    @org.junit.jupiter.api.Test
    void getUser() {
        User u = new User("joao_3" + id, "Joao Carlos", id, "3:" + id);

        User created = resource.create(u);

        assertEquals(u.censored(), created);
    }
}
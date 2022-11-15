package scc.srv;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.function.Executable;
import scc.data.Login;
import scc.data.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserResourceTest {

    static UserResource resource;

    @org.junit.jupiter.api.BeforeAll
    static void setUpping()
    {
        resource = new UserResource();
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.Test
    void create() {
        User u = random();
        User uCreated = resource.create(u.copy());

        assertEquals(u.censored(), uCreated.censored());

        assertThrows(ForbiddenException.class, () -> resource.create(u.copy()));
    }

    @org.junit.jupiter.api.Test
    void delete() {
        User u = random();
        Login l = new Login();
        l.setNickname(u.getNickname());
        l.setPwd(u.getPwd());

        Executable deleteUser = () -> resource.delete(resource.auth(l).getCookies().get("scc:session"), u.getNickname());

        assertThrows(NotFoundException.class, deleteUser);

        resource.create(u.copy());

        assertDoesNotThrow(deleteUser);
    }

    @org.junit.jupiter.api.Test
    void update() {
        User u = random();
        User u2 = random();

        resource.create(u.copy());
        User uGot = resource.update(u.getNickname(), u.getPwd(), u2.copy());
        User uExpected = u.patch(u2);

        assertEquals(uExpected.censored(), uGot.censored());
    }

    @org.junit.jupiter.api.Test
    void getUser() {
        User u = random();

        User got = resource.getUser(u.getNickname());

        assertEquals(u.censored(), got);
    }

    public static User createRandomUser()
    {
        User u = random();
        resource.create(u.copy());

        return u;
    }

    public static User random()
    {
        long id = System.currentTimeMillis();

        return new User("joao_" + id, "João Carlos",
                UUID.randomUUID().toString() + id, "3:" + id);
    }
}
package scc.session;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import scc.data.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class Session {

    private static final Algorithm signAlgorithm;

    static {

        try {
            InputStream fis = Session.class.getClassLoader().getResourceAsStream("authentication.properties");

            Properties props = new Properties();
            props.load(fis);

            signAlgorithm = Algorithm.HMAC512(props.getProperty("JWT_SECRET"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String token;

    public Session(User user)
    {
        token = JWT.create()
                .withIssuedAt(new Date())
                .withIssuer("scc")
                .withSubject(user.getNickname())
                .sign(signAlgorithm);
    }

    private Session(String token)
    {
        DecodedJWT jwt = JWT.decode(token);
    }

    public String getToken() {
        return token;
    }

    public static String getSignAlgorithm() {
        return signAlgorithm.getName();
    }
}

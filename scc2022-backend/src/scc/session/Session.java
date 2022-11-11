package scc.session;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.interfaces.Verification;
import jakarta.ws.rs.NotAuthorizedException;
import scc.data.User;

import java.io.IOException;
import java.io.InputStream;
import java.security.SignatureException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

public class Session {

    private static final String subject = "user:session";
    private static final String nicknameClaim = "nickname";

    private static final Algorithm signAlgorithm;
    private static final TemporalAmount validity;
    private static final String issuer;
    private static final JWTVerifier verifier;

    static {

        try {
            InputStream fis = Session.class.getClassLoader().getResourceAsStream("authentication.properties");

            Properties props = new Properties();
            props.load(fis);


            signAlgorithm = Algorithm.HMAC512(props.getProperty("JWT_SECRET"));
            issuer = props.getProperty("JWT_ISSUER");
            validity = Duration.ofMinutes(Long.parseLong(props.getProperty("JWT_VALIDITY")));
            verifier = JWT.require(signAlgorithm)
                    .withIssuer(issuer)
                    .withAudience(issuer)
                    .withSubject(subject)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final DecodedJWT token;


    private Session(DecodedJWT token)
    {
        this.token = token;
    }

    /**
     * Creates a new session for a user for authentication purposes
     * @param user the user
     */
    public static Session forUser(User user)
    {
        String token = JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date().toInstant().plus(validity))
                .withIssuer(issuer)
                .withAudience(issuer)
                .withSubject(subject)
                .withClaim(nicknameClaim, user.getNickname())
                .sign(signAlgorithm);

        return new Session(JWT.decode(token));
    }

    /**
     * Decodes a session from a token, checking its validity
     * @param token the session's string representation
     * @throws NotAuthorizedException if the token is invalid
     */
    public static Session fromToken(String token) throws NotAuthorizedException {
        try {
            return new Session(verifier.verify(token));
        } catch (Exception e)
        {
            throw new NotAuthorizedException(e);
        }
    }

    public Optional<String> getUserID() {
        return Optional.ofNullable(token.getClaim(nicknameClaim).asString());
    }

    public static String getSignAlgorithm() {
        return signAlgorithm.getName();
    }

    /**
     * @return the String representation of this session, via a token.
     */
    @Override
    public String toString() {
        return token.getToken();
    }

}

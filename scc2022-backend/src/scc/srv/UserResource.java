package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;


import scc.data.Auction;
import scc.data.DataProxy;
import scc.session.Login;
import scc.session.Session;
import scc.data.User;
import scc.utils.Hash;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Path("/user")
public class UserResource {

    private static final DataProxy dataProxy = DataProxy.getInstance();

    public UserResource() {}

    /**
     * Creates a new user
     * @param user the user information
     * @return the created user
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User create(User user) throws ForbiddenException
    {
        validateUserFields(user, true);

        if(!dataProxy.doesImageExist(user.getImageId()))
            throw new NotFoundException("User's picture is missing.");

        if (dataProxy.getUser(user.getNickname()).isPresent())
            throw new ForbiddenException("User already exists");

        if (dataProxy.createUser(user.hashPwd()).isPresent())
            return user;
        else
            return null;
    }

    /**
     * Deletes a user
     * @param nickname the user nickname
     */
    @DELETE
    @Path("/{nickname}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@CookieParam(Session.COOKIE_NAME) Cookie cookie, @PathParam("nickname") String nickname) {

        validateUserSession(cookie, nickname);

        dataProxy.deleteUser(nickname);
    }

    /**
     * Patches the details of a user
     * @param nickname the user nickname
     * @param newUser the user details to patch with
     * @return the updated user
     */
    @PATCH
    @Path("/{nickname}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User update(@PathParam("nickname") String nickname, @CookieParam(Session.COOKIE_NAME) Cookie cookie, User newUser)
    {
        validateUserFields(newUser, false);

        validateUserSession(cookie, nickname);

        Optional<User> prevUserDetails = dataProxy.getUser(nickname);

        if (prevUserDetails.isPresent() && Objects.nonNull(newUser.getImageId())
                && !prevUserDetails.get().getImageId().equals(newUser.getImageId()))
            if (!dataProxy.doesImageExist(newUser.getImageId()))
                throw new NotFoundException("User picture was not found.");

        return prevUserDetails.flatMap(user -> dataProxy.updateUserInfo(nickname, user.patch(newUser)))
                .orElse(null);
    }

    /**
     * @param nickname the user nickname
     * @return the details of the user with the given nickname
     * @throws NotFoundException if the user is not found
     */
    @GET
    @Path("/{nickname}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("nickname") String nickname) throws NotFoundException
    {
        Optional<User> userOptional = dataProxy.getUser(nickname);

        if (userOptional.isEmpty())
            throw new NotFoundException();

        return userOptional.map(User::censored).orElse(null);
    }

    /**
     * Shows the auctions of a user
     * @param nickname the nickname of the user
     * @return the user auctions
     * @throws WebApplicationException if there was an error finding the user
     */
    @GET
    @Path("/{nickname}/auctions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Auction> showUserAuctions(@PathParam("nickname") String nickname)
    {
        //Request to validate if user exists
        Optional<User> u = dataProxy.getUser(nickname);

        if(Objects.nonNull(u))
            return dataProxy.getAuctionsByUser(nickname);

        throw new NotFoundException("User not found");
    }

    /**
     * Authenticates the user by providing a cookie with previliges.
     * @param loginInfo the login details
     * @return the HTTP response, including the cookie
     */
    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response auth(Login loginInfo){

        User u = dataProxy.getUser(loginInfo.getNickname())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!u.getPwd().equals(Hash.of(loginInfo.getPwd())))
            throw new NotAuthorizedException("Password Incorrect.");

        String uid = UUID.randomUUID().toString();

        NewCookie cookie = new NewCookie.Builder(Session.COOKIE_NAME)
                .value(uid)
                .path("/rest/")
                .comment("sessionid")
                .maxAge(((int) Session.VALIDITY_SECONDS))
                .secure(false)
                .build();

        dataProxy.storeSession(cookie, loginInfo.getNickname());

        return Response.ok().cookie(cookie).build();
    }

    /**
     * Validates user details
     * @param user the user details
     * @param requireNonNull whether there can be null fields
     * @throws WebApplicationException if a field contains an invalid value
     */
    static void validateUserFields(User user, boolean requireNonNull) throws WebApplicationException
    {
        String error_message = null;

        if (Objects.isNull(user))
            error_message = "User can not be null";
        else {
            if (Objects.requireNonNullElse(user.getPwd(), requireNonNull ? "" : "a").isBlank())
                error_message = "Password can not be blank.";
            if (Objects.requireNonNullElse(user.getName(), requireNonNull ? "" : "a").isBlank())
                error_message = "Name can not be blank.";
            if (Objects.requireNonNullElse(user.getNickname(), requireNonNull ? "" : "a").isBlank())
                error_message = "Nickname can not be blank.";
        }

        if (Objects.nonNull(error_message))
            throw new BadRequestException(error_message);
    }

    /**
     * Checks if the given cookie authenticates the user with the given nickname
     * @param cookie the auth cookie
     * @param nickname the user nickname
     * @throws WebApplicationException if the session is not valid
     */
    public static void validateUserSession(Cookie cookie, String nickname){
        if (Objects.isNull(nickname))
            throw new BadRequestException("Can't validate session if nickname is not provided.");

        if(Objects.isNull(cookie))
            throw new NotAuthorizedException("No session cookie found.");

        if (Objects.isNull(cookie.getValue()))
            throw new NotAuthorizedException("Session cookie is invalid");

        Session s = dataProxy.getSession(nickname)
                .orElseThrow(() -> new NotAuthorizedException("Session not found. Have you tried getting a session cookie?"));

        if(!nickname.equals(s.getNickname()) || !cookie.getValue().equals(s.getCookieId()))
            throw new NotAuthorizedException("No ~valid~ session initialized.");
    }
}

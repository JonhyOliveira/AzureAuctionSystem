package scc.srv;

import jakarta.ws.rs.*;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;


import scc.data.DataProxy;
import scc.data.Login;
import scc.data.SessionTemp;
import scc.data.User;
import scc.session.Session;
import scc.utils.Hash;

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
     * @throws ForbiddenException if the
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User create(User user) throws ForbiddenException
    {
        validateUserFields(user, true);

        if(!dataProxy.doesFileExist(user.getPhotoId()))
            throw new NotFoundException("User's picture is missing.");

        if (dataProxy.getUser(user.getNickname()).isPresent())
            throw new ForbiddenException("User already exists");

        user.setPwd(Hash.of(user.getPwd()));

        return dataProxy.createUser(user)
                .map(User::censored)
                .orElse(null);

    }

    /**
     * Deletes a user
     * //@param nickname the user nickname
     * //@param password the user password
     */
    /*@DELETE
    @Path("/{nickname}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("nickname") String nickname, @HeaderParam("Authorization") String password)
    {
        if (Objects.requireNonNullElse(password, "").isBlank())
            throw new BadRequestException("Password can not be blank");

        Optional<User> o = dataProxy.getUser(nickname);

        if (o.isEmpty())
            throw new NotFoundException("User not found");

        if (! o.get().getPwd().equals(Hash.of(password)))
            throw new NotAuthorizedException("Password Incorrect");

        dataProxy.deleteUser(nickname);
    }*/

    @DELETE
    @Path("/{nickname}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@CookieParam("scc:session") Cookie cookie, @PathParam("nickname") String nickname) {

        SessionTemp s = getUserSession(cookie);

        if(!s.getNickname().equals(nickname))
            throw new NotAuthorizedException("Invalid user:" + s.getNickname());

        // ! TODO ACHO QUE TBM A PARTIR DO MOMENTO EM QUE FAZEMOS A VERIFICAÇAO SE A SESSAO É VALIDA NAO NECESSITAMOS
        // TODO DE IR BUSCAR E VERIFICAR SE O USER EXISTE, POIS ESTAMOS A ELEMINAR O NOSSO PROPRIO USER
        //Optional<User> o = dataProxy.getUser(nickname);

        /*if (o.isEmpty())
            throw new NotFoundException("User not found");*/

        dataProxy.deleteUser(nickname);
    }

    public SessionTemp getUserSession(Cookie cookie){
        if(Objects.isNull(cookie) || Objects.isNull(cookie.getValue()))
            throw new NotAuthorizedException("No session initialiazed.");

        SessionTemp s = dataProxy.getSession(cookie);

        if(Objects.isNull(s) || Objects.isNull(s.getNickname()) || s.getNickname().length()==0)
            throw new NotAuthorizedException("No valid session initializded.");

        if(!s.getCookieId().equals(cookie.getValue()))
            throw new InternalServerErrorException("Unexpected session value");

        return s;
    }

    /**
     * Patches the details of a user
     * @param nickname the user nickname
     * @param password the user password
     * @param newUser the user details to patch with
     * @return the updated user
     */
    @PATCH
    @Path("/{nickname}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User update(@PathParam("nickname") String nickname, @HeaderParam("Authorization") String password, User newUser)
    {
        if (Objects.requireNonNullElse(password, "").isBlank())
            throw new BadRequestException("Password can not be blank");

        validateUserFields(newUser, false);

        User prevUserDetails = login(nickname, password);

        return dataProxy.updateUserInfo(nickname, prevUserDetails.patch(newUser))
                .map(User::censored)
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
     * Validates user details
     * @param user the user details
     * @param requireNonNull whether there can be null fields
     * @throws WebApplicationException if a field contains an invalid value
     */
    void validateUserFields(User user, boolean requireNonNull) throws WebApplicationException
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

    // ! TORNAR-SE-Á OBSOLETO AO PASSARMOS A UTILIZAR A FUNÇÃO DE BAIXO (AUTH)
    /**
     * Logs in an user
     * @param nickname the user nickname
     * @param password the user password
     * @return the user details
     */
    User login(String nickname, String password)
    {
        Optional<User> o = dataProxy.getUser(nickname);

        if (o.isEmpty())
            throw new NotFoundException("User not found");

        if (! o.get().getPwd().equals(Hash.of(password)))
            throw new NotAuthorizedException("Password Incorrect.");

        return o.get();
    }

    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response auth(Login loginInfo){

        if(dataProxy.verifyLogin(loginInfo.getNickname(), loginInfo.getPwd())){
            String uid = UUID.randomUUID().toString();

            NewCookie cookie = new NewCookie.Builder("scc:session")
                    .value(uid)
                    .path("/")
                    .comment("sessionid")
                    .maxAge(SessionTemp.VALIDITY_SECONDS)
                    .secure(false)
                    .httpOnly(true)
                    .build();

            dataProxy.storeCookie(cookie, loginInfo.getNickname());

            return Response.ok().cookie(cookie).build();
        } else
            throw new NotAuthorizedException("Incorrect Login");
    }
}

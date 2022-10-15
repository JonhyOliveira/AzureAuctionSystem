package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.data.DataProxy;
import scc.data.User;
import scc.utils.Hash;

import java.util.Objects;
import java.util.Optional;

@Path("/user")
public class UserResource {

    private static final DataProxy dataProxy = DataProxy.getInstance();

    public UserResource() {}

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User create(User user)
    {
        validateUserFields(user, true);

        if (dataProxy.getUser(user.nickname()).isPresent())
            throw new ForbiddenException("User already exists");

        user.setPwd(Hash.of(user.pwd()));

        return dataProxy.createUser(user)
                .map(User::censored)
                .orElse(null);

    }

    @DELETE
    @Path("/{nickname}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("nickname") String nickname, @HeaderParam("Authorization") String password)
    {
        if (Objects.requireNonNullElse(password, "").isBlank())
            throw new BadRequestException("Password can not be blank");

        Optional<User> o = dataProxy.getUser(nickname);

        if (o.isEmpty())
            throw new NotFoundException("User not found");

        if (! o.get().pwd().equals(Hash.of(password)))
            throw new NotAuthorizedException("Password Incorrect");

        dataProxy.deleteUser(nickname);
    }

    @PUT
    @Path("/{nickname}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User update(@PathParam("nickname") String nickname, @HeaderParam("Authorization") String password, User newUser)
    {
        if (Objects.requireNonNullElse(password, "").isBlank())
            throw new BadRequestException("Password can not be blank");

        validateUserFields(newUser, false);

        Optional<User> o = dataProxy.getUser(nickname);

        if (o.isEmpty())
            throw new NotFoundException("User not found");

        if (! o.get().pwd().equals(Hash.of(password)))
            throw new NotAuthorizedException("Password Incorrect.");

        return dataProxy.updateUserInfo(nickname, newUser)
                .map(User::censored)
                .orElse(null);
    }

    @GET
    @Path("/{nickname}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("nickname") String nickname)
    {
        Optional<User> userOptional = dataProxy.getUser(nickname);

        if (userOptional.isEmpty())
            throw new NotFoundException();

        return userOptional.map(User::censored).orElse(null);
    }

    /**
     * Validates user fields
     * @throws WebApplicationException if a field contains an invalid
     */
    void validateUserFields(User user, boolean requireNonNull) throws WebApplicationException
    {
        String error_message = null;

        if (Objects.isNull(user))
            error_message = "User can not be null";
        else {
            if (Objects.requireNonNullElse(user.pwd(), requireNonNull ? "" : "a").isBlank())
                error_message = "Password can not be blank.";
            if (Objects.requireNonNullElse(user.name(), requireNonNull ? "" : "a").isBlank())
                error_message = "Name can not be blank.";
            if (Objects.requireNonNullElse(user.nickname(), requireNonNull ? "" : "a").isBlank())
                error_message = "Nickname can not be blank.";
        }

        if (Objects.nonNull(error_message))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode(), error_message).build());
    }

}

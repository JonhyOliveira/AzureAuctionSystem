package scc.srv;

import com.azure.cosmos.models.CosmosItemResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.database.CosmosDBLayer;
import scc.database.User;
import scc.database.UserDAO;
import scc.utils.Hash;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Path("/user")
public class UserResource {

    private static final CosmosDBLayer dbLayer = CosmosDBLayer.getInstance();

    public UserResource() {}

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User create(User user)
    {
        validateUserFields(user, true);

        if (dbLayer.getUserByNick(user.getNickname()).stream().findAny().isPresent())
            throw new BadRequestException("User already exists");

        user.setPwd(Hash.of(user.getPwd()));
        UserDAO newUser = new UserDAO(user);

        return dbLayer.putUser(newUser).getItem().toUser().censored();

    }

    @DELETE
    @Path("/{nickname}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("nickname") String nickname, @HeaderParam("Authorization") String password)
    {
        if (Objects.requireNonNullElse(password, "").isBlank())
            throw new BadRequestException("Password can not be blank");

        Optional<UserDAO> o = dbLayer.getUserByNick(nickname).stream().findAny();

        if (o.isEmpty())
            throw new NotFoundException("User not found");

        if (! o.get().getPwd().equals(Hash.of(password)))
            throw new NotAuthorizedException("Password Incorrect");

        dbLayer.delUserByNick(nickname);
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

        Optional<UserDAO> o = dbLayer.getUserByNick(nickname).stream().findAny();

        if (o.isEmpty())
            throw new NotFoundException("User not found");

        if (! o.get().getPwd().equals(Hash.of(password)))
            throw new NotAuthorizedException("Password Incorrect");

        return dbLayer.updateUser(nickname, o.get().update(newUser)).getItem().toUser().censored();
    }

    @GET
    @Path("/{nickname}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("nickname") String nickname)
    {
        Optional<UserDAO> o = dbLayer.getUserByNick(nickname).stream().findAny();

        if (o.isEmpty())
            throw new NotFoundException();

        return o.get().toUser().censored();
    }

    /**
     * Validates user fields
     * @throws WebApplicationException if a field contains an invalid
     */
    private void validateUserFields(User user, boolean requireNonNull) throws WebApplicationException
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
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode(), error_message).build());
    }

}

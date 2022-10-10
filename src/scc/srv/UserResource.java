package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import scc.database.CosmosDBLayer;
import scc.database.User;

import java.util.List;

@Path("/user")
public class UserResource {

    private static final CosmosDBLayer dbLayer = CosmosDBLayer.getInstance();

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public void create(User user)
    {
        throw new NotSupportedException();
    }

    @DELETE
    @Path("/{nickname}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void delete(@PathParam("nickname") String nickname, String password)
    {
        throw new NotSupportedException();
    }

    @PUT
    @Path("/{nickname}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void update(@PathParam("nickname") String nickname, String password, User newUser)
    {
        throw new NotSupportedException();
    }

}

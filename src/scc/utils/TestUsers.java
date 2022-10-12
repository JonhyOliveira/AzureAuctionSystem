package scc.utils;

import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import scc.database.CosmosDBLayer;
import scc.database.UserDAO;

import java.util.Locale;

/**
 * Standalone program for accessing the database
 *
 */
public class TestUsers
{
	public static void main(String[] args) {

		try {
			Locale.setDefault(Locale.US);
			CosmosDBLayer db = CosmosDBLayer.getInstance();
			String id = "0:" + System.currentTimeMillis();
			CosmosItemResponse<UserDAO> res = null;
			UserDAO u = new UserDAO();
			u.setId(id);
			u.setName("SCC " + id);
			u.setPwd("super_secret");
			u.setPhotoId("0:34253455");

			System.out.println("Putting user...");
			res = db.putUser(u);
			System.out.println( "Put result");
			System.out.println( res.getStatusCode());
			System.out.println( res.getItem());

			System.out.println( "Get for id = " + id);
			CosmosPagedIterable<UserDAO> resGet = db.getUserByNick(id);
			for( UserDAO e: resGet) {
				System.out.println( e);
			}

			System.out.println( "Get for all ids");
			resGet = db.getUsers();
			for( UserDAO e: resGet) {
				System.out.println( e);
			}

			// Now, let's create and delete
			id = "0:" + System.currentTimeMillis();
			res = null;
			u = new UserDAO();
			u.setId(id);
			u.setName("SCC " + id);
			u.setPwd("super_secret");
			u.setPhotoId("0:34253455");

			res = db.putUser(u);
			System.out.println( "Put result");
			System.out.println( res.getStatusCode());
			System.out.println( res.getItem());
			System.out.println( "Get for id = " + id);

			System.out.println( "Get by id result");
			resGet = db.getUserByNick(id);
			for( UserDAO e: resGet) {
				System.out.println( e);
			}

			System.out.println( "Delete user with id = " + id);
			db.delUserByNick(id);

			// db.putUser(new UserDAO("jota", "João Oliveira", "1234", ""));

			System.out.println(db.updateUser("jota", u).getItem().toString());

			System.out.println( "Get by id result");
			resGet = db.getUserByNick(id);
			for( UserDAO e: resGet) {
				System.out.println( e);
			}



			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



package scc.srv;

import jakarta.ws.rs.*;
import scc.utils.Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.MediaType;

/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource
{
	Map<String,byte[]> map = new HashMap<String,byte[]>();

	/**
	 * Post a new image.The id of the image is its hash.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public String upload(byte[] contents) {
		String key = Hash.of(contents);
		map.put( key, contents);
		return key;
	}

	/**
	 * Return the contents of an image. Throw an appropriate error message if
	 * id does not exist.
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] download(@PathParam("id") String id) {
		//throw new ServiceUnavailableException();
		if (!map.containsKey(id))
			throw new ProcessingException("Not found");
		return map.get(id);
	}

	private static final String FILE_LIST_FMT = String.format("file id: %%-%ds - %%d bytes long", Hash.HASH_LENGTH);

	/**
	 * Lists the ids of images stored.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String list() {
		String res = map.keySet().stream().map(s -> String.format(FILE_LIST_FMT, s, map.get(s).length)).collect(Collectors.joining("\n"));
		if (res.isBlank())
			return "No content found.";
		else
			return res;
	}
}

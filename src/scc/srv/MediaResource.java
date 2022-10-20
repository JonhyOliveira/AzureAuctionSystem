package scc.srv;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import jakarta.ws.rs.*;
import scc.utils.Hash;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.MediaType;

/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource {

	private final BlobContainerClient containerClient;

	{
		try {
			InputStream fis = this.getClass().getClassLoader().getResourceAsStream("blobstore.properties");
			Properties props = new Properties();

			props.load(fis);

			containerClient = new BlobContainerClientBuilder()
					.connectionString(props.getProperty("CONN_STRING"))
					.containerName("images")
					.buildClient();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public MediaResource() {}

	/**
	 * Post a new file
	 * @return the id of the posted file
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.TEXT_PLAIN)
	public String upload(byte[] contents) {
		String key = Hash.of(contents);
		BlobClient blob = containerClient.getBlobClient(key);
		if (!blob.exists())
			blob.upload(BinaryData.fromBytes(contents));
		return key;
	}

	/**
	 * Returns the contents of a file
	 * @param id the file id
	 * @throws NotFoundException if there is no file with the given id
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] download(@PathParam("id") String id) {
		BlobClient blob = containerClient.getBlobClient(id);
		if (!blob.exists())
			throw new NotFoundException();
		return blob.downloadContent().toBytes();
	}

	private static final String FILE_LIST_FMT = String.format("file id: %%-%ds - %%d bytes long", Hash.HASH_LENGTH);

	/**
	 * Lists the ids of files stored.
	 * @return list of stored files ids
	 */
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public String list() {
		String res = containerClient.listBlobs().stream()
				.map(blobItem -> String.format(FILE_LIST_FMT, blobItem.getName(), blobItem.getProperties().getContentLength()))
				.collect(Collectors.joining("\n"));
		if (res.isBlank())
			return "No content found.";
		else
			return res;
	}
}

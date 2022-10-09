package scc.srv;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
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
	public static final String blobStoreConString = "DefaultEndpointsProtocol=https;AccountName=scc222358001;AccountKey=jml3xMDPLhWu8UYUMYbB6+CFI7qY2BDO6mI9aw9g0oWSShxEGDwuyXQiKHXTAyb1Atk0yX3PJVtG+AStxRY2LQ==;EndpointSuffix=core.windows.net";

	Map<String,byte[]> map = new HashMap<String,byte[]>();
	BlobContainerClient containerClient = new BlobContainerClientBuilder()
			.connectionString(blobStoreConString)
			.containerName("images")
			.buildClient();

	/**
	 * Post a new image.The id of the image is its hash.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.TEXT_PLAIN)
	public String upload(byte[] contents) {
		String key = Hash.of(contents);
		BlobClient blob = containerClient.getBlobClient(key);
		if (blob.exists())
			throw new ForbiddenException("Can't overwrite file '%s'.".formatted(key));
		blob.upload(BinaryData.fromBytes(contents));
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
		BlobClient blob = containerClient.getBlobClient(id);
		if (!blob.exists())
			throw new NotFoundException();
		return blob.downloadContent().toBytes();
	}

	private static final String FILE_LIST_FMT = String.format("file id: %%-%ds - %%d bytes long", Hash.HASH_LENGTH);

	/**
	 * Lists the ids of images stored.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
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
package scc.srv;

import jakarta.ws.rs.*;
import scc.data.DataProxy;
import scc.data.layers.BlobStorageLayer;
import scc.utils.Hash;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.MediaType;

/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource {

	static BlobStorageLayer blobStorage = BlobStorageLayer.getInstance();
	private static final DataProxy dataProxy = DataProxy.getInstance();

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
		String fileID = Hash.of(contents);

		if (!dataProxy.doesFileExist(fileID))
			dataProxy.uploadFile(fileID, contents);

		return fileID;
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
		byte[] data = dataProxy.downloadFile(id);

		if (data == null)
			throw new NotFoundException("Image not found.");

		return data;
	}

	private static final String FILE_LIST_FMT = String.format("file id: %%-%ds - %%d bytes long", Hash.HASH_LENGTH);

	/**
	 * Lists the ids of files stored.
	 * @return list of stored files ids
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String[] list() {
		return dataProxy.listFiles().toArray(new String[0]);
	}
}

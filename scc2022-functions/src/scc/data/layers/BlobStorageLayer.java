package scc.data.layers;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class BlobStorageLayer {

    private static BlobStorageLayer instance;

    public final BlobContainerClient containerClient;

    public BlobStorageLayer(String connString) {
        containerClient = new BlobContainerClientBuilder()
                .connectionString(connString)
                .containerName("images")
                .buildClient();
    }

    public static BlobStorageLayer getInstance() {
        if (instance == null)
            instance = new BlobStorageLayer(System.getenv("BLOBSTORE_CONNSTRING"));

        return instance;
    }

    public byte[] downloadBlob(String blobID)
    {
        BlobClient blob = containerClient.getBlobClient(blobID);
        if (!blob.exists())
            return null;

        return blob.downloadContent().toBytes();
    }

    public boolean blobExists(String blobID)
    {
        return containerClient.getBlobClient(blobID).exists();
    }

    public void createBlob(String blobID, byte[] data)
    {
        containerClient.getBlobClient(blobID).upload(BinaryData.fromBytes(data));
    }

    public Stream<String> listFiles() {
        return containerClient.listBlobs().stream().map(BlobItem::getName);
    }

    public void deleteBlob(String blobID){ containerClient.getBlobClient(blobID).deleteIfExists();}
}

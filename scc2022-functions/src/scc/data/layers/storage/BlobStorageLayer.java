package scc.data.layers.storage;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;

import java.util.stream.Stream;

public class BlobStorageLayer implements StorageLayer {

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

    @Override
    public byte[] downloadFile(String blobID)
    {
        BlobClient blob = containerClient.getBlobClient(blobID);
        if (!blob.exists())
            return null;

        return blob.downloadContent().toBytes();
    }

    @Override
    public boolean fileExists(String blobID)
    {
        return containerClient.getBlobClient(blobID).exists();
    }

    public void createFile(String blobID, byte[] data)
    {
        containerClient.getBlobClient(blobID).upload(BinaryData.fromBytes(data));
    }

    @Override
    public Stream<File> listFiles() {
        return containerClient.listBlobs().stream().map(File::fromBlob);
    }

    @Override
    public void deleteFile(String blobID){ containerClient.getBlobClient(blobID).deleteIfExists();}
}

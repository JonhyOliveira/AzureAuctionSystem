package scc.data.layers.storage;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

import java.util.stream.Stream;

public class BlobStorageLayer implements StorageLayer {

    private static BlobStorageLayer instance;

    public final BlobContainerClient containerClient;

    public static BlobStorageLayer getInstance() {
        if (instance == null)
            instance = new BlobStorageLayer(System.getenv("BLOBSTORE_CONNSTRING"));

        return instance;
    }

    public BlobStorageLayer(String connString) {
        containerClient = new BlobContainerClientBuilder()
                .connectionString(connString)
                .containerName("images")
                .buildClient();
    }

    @Override
    public byte[] downloadFile(String fileID)
    {
        BlobClient blob = containerClient.getBlobClient(fileID);
        if (!blob.exists())
            return null;

        return blob.downloadContent().toBytes();
    }

    @Override
    public boolean fileExists(String fileID)
    {
        return containerClient.getBlobClient(fileID).exists();
    }

    @Override
    public void createFile(String fileID, byte[] data)
    {
        containerClient.getBlobClient(fileID).upload(BinaryData.fromBytes(data));
    }

    @Override
    public Stream<File> listFiles() {
        return containerClient.listBlobs().stream().map(File::fromBlob);
    }

    @Override
    public void deleteFile(String fileID){ containerClient.getBlobClient(fileID).delete();}
}

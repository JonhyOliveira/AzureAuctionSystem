package scc.data.layers;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import jakarta.ws.rs.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BlobStorageLayer {

    private static BlobStorageLayer instance;

    public final BlobContainerClient containerClient;

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

    public static BlobStorageLayer getInstance() {
        if (instance == null)
            instance = new BlobStorageLayer();

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

}

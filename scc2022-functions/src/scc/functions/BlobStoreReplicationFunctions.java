package scc.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import scc.data.layers.BlobStorageLayer;

public class BlobStoreReplicationFunctions {

    @FunctionName("BlobCreation-Java")
    public void blobCreationToReplicated( @BlobTrigger(
            name = "blobTriggerEUWEST",
            path = "images/{name}", //presumo que seja este o path
            dataType = "binary",
            connection = "BLOBSTORE_CONNSTRING")
                                          byte[] content,
                                          @BindingName("name") String blobname,
                                          final ExecutionContext context) {
        context.getLogger().info(String.format("Blob %s updated, replicating..", blobname));

        BlobStorageLayer blobStorageLayer = new BlobStorageLayer(System.getenv("BLOBSTORE_STRINGREP"));
        if (!blobStorageLayer.blobExists(blobname))
            blobStorageLayer.createBlob(blobname, content);

    }

    @FunctionName("BlobCreationReplicated-Java")
    public void blobCreationBack( @BlobTrigger(
            name = "blobTriggerEUNORTH",
            path = "images/{name}", //presumo que seja este o path
            dataType = "binary",
            connection = "BLOBSTORE_STRINGREP")
                                  byte[] content,
                                  @BindingName("name") String blobname,
                                  final ExecutionContext context) {
        context.getLogger().info(String.format("Blob %s updated, replicating..", blobname));

        BlobStorageLayer blobStorageLayer = new BlobStorageLayer(System.getenv("BLOBSTORE_CONNSTRING"));
        if (!blobStorageLayer.blobExists(blobname))
            blobStorageLayer.createBlob(blobname, content);
    }

}

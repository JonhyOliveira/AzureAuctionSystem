package scc.data.layers.storage;

import com.azure.storage.blob.models.BlobItem;

public record File(String fileID, Long fileSize) {

    static File fromBlob(BlobItem blobItem) {
        return new File(blobItem.getName(), blobItem.getProperties().getContentLength());
    }

}

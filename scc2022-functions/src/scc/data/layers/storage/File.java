package scc.data.layers.storage;

import com.azure.storage.blob.models.BlobItem;

public class File {

    private String fileID;
    private Long fileSize;

    public File(String fileID, Long fileSize) {
        this.fileID = fileID;
        this.fileSize = fileSize;
    }

    static File fromBlob(BlobItem blobItem) {
        return new File(blobItem.getName(), blobItem.getProperties().getContentLength());
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}

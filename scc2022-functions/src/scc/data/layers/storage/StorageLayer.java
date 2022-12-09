package scc.data.layers.storage;

import java.util.stream.Stream;

public interface StorageLayer {
    byte[] downloadFile(String fileID);

    boolean fileExists(String fileID);

    void createFile(String fileID, byte[] data);

    Stream<File> listFiles();

    void deleteFile(String fileID);
}

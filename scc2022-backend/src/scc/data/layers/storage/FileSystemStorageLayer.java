package scc.data.layers.storage;

import jakarta.ws.rs.NotAuthorizedException;
import scc.data.layers.db.MongoDBLayer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileSystemStorageLayer implements StorageLayer {

    private static final Logger logger = Logger.getLogger(FileSystemStorageLayer.class.getName());
    private static FileSystemStorageLayer instance;

    public static FileSystemStorageLayer getInstance() {
        if (instance == null)
            instance = new FileSystemStorageLayer();
        return instance;
    }

    private final java.io.File directory = new java.io.File(System.getenv("BLOBSTORE_CONNSTRING"));

    private FileSystemStorageLayer() {
        if (!directory.exists()) {
            try {
                Files.createDirectory(directory.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!directory.isDirectory())
            throw new IllegalArgumentException("Path must specify a directory");

        logger.warning("Connected.");
    }

    @Override
    public byte[] downloadFile(String fileID) {
        try (FileInputStream fis = new FileInputStream(new java.io.File(directory, fileID))) {
            return fis.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean fileExists(String fileID) {
        return new java.io.File(directory, fileID).exists();
    }

    @Override
    public void createFile(String fileID, byte[] data) {
        if (!fileExists(fileID)) {
            try (FileOutputStream fos = new FileOutputStream(new java.io.File(directory, fileID))) {
                fos.write(data);
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new NotAuthorizedException("You can't overwrite an exinting file.");
    }

    @Override
    public Stream<File> listFiles() {
        return Stream.of(directory.listFiles())
                .map(file -> {
                    try {
                        if (file.exists() && file.isFile())
                            return new File(file.getName(), Files.size(file.toPath()));
                    } catch (IOException ignored) {}

                    return null;
                })
                .filter(Objects::nonNull);
    }

    @Override
    public void deleteFile(String fileID) {
        Optional.of(new java.io.File(directory, fileID))
                .filter(java.io.File::exists)
                .filter(java.io.File::isFile)
                .ifPresent(java.io.File::delete);
    }

}

package org.example.storage.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class LocalFileRepository implements FileRepository {

    private final Path storageDirectory;

    public LocalFileRepository(Path storageDirectory) throws IOException {
        this.storageDirectory = storageDirectory.toAbsolutePath()
                .normalize();

        Files.createDirectories(this.storageDirectory);
    }

    @Override
    public void save(String id, InputStream content) throws IOException {
        Path destination = storageDirectory.resolve(id).normalize();

        if (!destination.getParent().equals(storageDirectory)) {
            throw new IOException("Invalid file identifier");
        }

        Files.copy(content, destination, StandardCopyOption.REPLACE_EXISTING);
    }
}

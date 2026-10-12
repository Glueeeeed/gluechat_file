package org.gluechat.storage.file;
import org.gluechat.storage.common.NotFoundError;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

public class FileService {
    private final FileRepository repository;

    public FileService(FileRepository repository) {
        this.repository = repository;
    }

    public FileMetadataResponse upload(InputStream content, String extension) throws IOException {
        String id = UUID.randomUUID().toString();

        String ext = (extension != null && !extension.isBlank())
                ? (extension.startsWith(".") ? extension : "." + extension)
                : "";

        String storedFileName = id + ext;
        repository.save(storedFileName, content);
        return new FileMetadataResponse(storedFileName);
    }

    public Path download(String filename) throws NotFoundError, IOException {
        return repository.download(filename);
    }

    public void delete(String filename) throws NotFoundError, IOException {
        repository.delete(filename);
    }
}
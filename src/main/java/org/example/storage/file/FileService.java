package org.example.storage.file;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class FileService {
    private final FileRepository repository;
    public FileService(FileRepository repository) {
        this.repository = repository;
    }
    public FileMetadata upload(InputStream content, String originalFilename, String extension, long size) throws IOException {
        String id = UUID.randomUUID().toString();

        String ext = (extension != null && !extension.isBlank())
                ? (extension.startsWith(".") ? extension : "." + extension)
                : "";

        String storedFileName = id + ext;
        repository.save(storedFileName, content);
        return new FileMetadata(id, originalFilename, ext, size);
    }
}
package org.gluechat.storage.file;
import org.gluechat.storage.common.NotFoundError;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HexFormat;
import java.security.SecureRandom;

public class FileService { ;
    private final FileRepository repository;
    private final SecureRandom secureRandom;

    public FileService(FileRepository repository, SecureRandom secureRandom) {
        this.repository = repository;
        this.secureRandom = secureRandom;
    }

    public FileMetadataResponse upload(InputStream content, String extension, String originalFileName) throws IOException {
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        String hexRandomBytes = HexFormat.of().formatHex(randomBytes);

        String normalizedFileName = normalizeFileName(originalFileName);

        String filename =  normalizedFileName + "|" + hexRandomBytes;
        repository.save(filename, content);
        return new FileMetadataResponse(filename);
    }

    private String normalizeFileName(String fileName) {
        String normalized = fileName.replace('|', '_').replace(' ', '_');
        if (normalized.length() > 25) {
            normalized = normalized.substring(0, 25);
        }
        return normalized;
    }

    public Path download(String filename) throws NotFoundError, IOException {
        return repository.download(filename);
    }

    public void delete(String filename) throws NotFoundError, IOException {
        repository.delete(filename);
    }
}
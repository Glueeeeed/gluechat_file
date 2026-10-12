package org.example.storage.file;

public record FileMetadata(
        String id,
        String fileName,
        String extension,
        long size
) {}

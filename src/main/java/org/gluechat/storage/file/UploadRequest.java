package org.gluechat.storage.file;

public record UploadRequest(
    String file64,
    String fileName
) {}

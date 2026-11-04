package org.gluechat.storage.file;

public record DownloadResponse(
    String file64,
    String filename
) {}


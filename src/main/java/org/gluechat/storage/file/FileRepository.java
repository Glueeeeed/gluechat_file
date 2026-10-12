package org.gluechat.storage.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface FileRepository {
    void save(String id, InputStream content) throws IOException;
    Path download(String filename) throws IOException;
    void delete(String filename) throws IOException;
}

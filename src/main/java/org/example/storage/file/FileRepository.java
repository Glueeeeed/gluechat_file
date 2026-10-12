package org.example.storage.file;

import java.io.IOException;
import java.io.InputStream;

public interface FileRepository {
    void save(String id, InputStream content) throws IOException;
}

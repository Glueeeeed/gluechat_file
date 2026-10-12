package org.example.storage.file;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.HttpStatus;
import io.javalin.http.UploadedFile;
public class FileController {
    private final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    public void upload(Context ctx) throws Exception {
        UploadedFile uploadedFile = ctx.uploadedFile("file");
        if (uploadedFile == null) {
            throw new HttpResponseException(HttpStatus.BAD_REQUEST.getCode(), "File part 'file' is required");
        }
        if (uploadedFile.size() > 50L * 1024 * 1024) {
            throw new HttpResponseException(413, "File too large");
        }
        FileMetadata metadata = fileService.upload(
                uploadedFile.content(),
                uploadedFile.filename(),
                uploadedFile.extension(),
                uploadedFile.size()
        );
        ctx.status(HttpStatus.CREATED).json(metadata);
    }
}
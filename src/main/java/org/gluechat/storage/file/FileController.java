package org.gluechat.storage.file;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.HttpStatus;
import io.javalin.http.UploadedFile;
import org.gluechat.storage.common.NotFoundError;

import java.nio.file.Files;
import java.nio.file.Path;

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
        FileMetadataResponse metadata = fileService.upload(
                uploadedFile.content(),
                uploadedFile.extension()
        );
        ctx.status(HttpStatus.CREATED).json(metadata);
    }

    public void download(Context ctx) throws Exception {
         try {
             FileRequest request = ctx.bodyValidator(FileRequest.class)
                     .check(dto -> dto.filename() != null && !dto.filename().isBlank(), "filename cannot be empty")
                     .check(dto -> dto.filename().length() <= 50, "Filename is too long (max filename length 50 characters)")
                     .get();

             Path filePath = fileService.download(request.filename());


             ctx.header("Content-Disposition", "attachment; filename=\"" + request.filename() + "\"");

             ctx.contentType("application/octet-stream");

             ctx.result(Files.newInputStream(filePath));

         } catch (NotFoundError e) {
             ctx.status(HttpStatus.NOT_FOUND);
             ctx.result(e.getMessage());
         }
    }

    public void delete(Context ctx) throws Exception {
        try {
            FileRequest request = ctx.bodyValidator(FileRequest.class)
                    .check(dto -> dto.filename() != null && !dto.filename().isBlank(), "filename cannot be empty")
                    .get();

            fileService.delete(request.filename());
            ctx.status(200);
        } catch (NotFoundError e) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(e.getMessage());
        }
    }
}
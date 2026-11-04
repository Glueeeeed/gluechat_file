package org.gluechat.storage.file;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.gluechat.storage.common.NotFoundError;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class FileController {
    private final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    public void upload(Context ctx) throws Exception {
        UploadRequest request = ctx.bodyValidator(UploadRequest.class)
                .check(dto -> dto.file64() != null && !dto.file64().isBlank(), "file64 cannot be empty")
                .check(dto -> dto.fileName() != null && !dto.fileName().isBlank(), "fileName cannot be empty")
                .get();

        byte[] fileBytes = Base64.getDecoder().decode(request.file64());
        if (fileBytes.length > 50L * 1024 * 1024) {
            ctx.status(413);
            ctx.result("File too large");
            return;
        }

        String extension = "";
        int dotIndex = request.fileName().lastIndexOf('.');
        if (dotIndex > 0) {
            extension = request.fileName().substring(dotIndex + 1);
        }

        FileMetadataResponse metadata = fileService.upload(
                new java.io.ByteArrayInputStream(fileBytes),
                extension,
                request.fileName()
        );
        ctx.status(HttpStatus.CREATED).json(metadata);
    }

    public void download(Context ctx) throws Exception {
        try {
            FileRequest request = ctx.bodyValidator(FileRequest.class)
                    .check(dto -> dto.filename() != null && !dto.filename().isBlank(), "filename cannot be empty")
                    .check(dto -> dto.filename().length() <= 100, "Filename is too long (max filename length 100 characters)")
                    .get();

            Path filePath = fileService.download(request.filename());

            // Read file bytes and encode to base64
            byte[] fileBytes = Files.readAllBytes(filePath);
            String base64File = Base64.getEncoder().encodeToString(fileBytes);

            ctx.json(new DownloadResponse(base64File, request.filename()));

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
import io.javalin.Javalin;
import io.javalin.config.SizeUnit;
import org.example.storage.common.GlobalExceptionHandler;
import org.example.storage.file.*;

import java.io.IOException;
import java.nio.file.Path;

import static io.javalin.apibuilder.ApiBuilder.*;


void main() throws IOException {
    FileRepository repository = new LocalFileRepository(Path.of("storage-data"));

    FileService service = new FileService(repository);
    FileController controller = new FileController(service);


    Javalin app = Javalin.create(config -> {
        config.jetty.multipartConfig.maxFileSize(50, SizeUnit.MB);
        config.routes.apiBuilder(() -> {
            path("api", () -> {
                path("files", () -> {
                    post(controller::upload);
                });
            });
        });

        GlobalExceptionHandler.register(config.routes);
    });

    app.start(7070);

    System.out.println("GlueChat Storage Api started");
}

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.config.SizeUnit;
import org.gluechat.storage.common.GlobalExceptionHandler;
import org.gluechat.storage.file.*;
import org.gluechat.storage.file.FileController;
import org.gluechat.storage.file.FileRepository;
import org.gluechat.storage.file.FileService;
import org.gluechat.storage.file.LocalFileRepository;

import java.io.IOException;
import java.nio.file.Path;

import static io.javalin.apibuilder.ApiBuilder.*;


void main() throws IOException {
    Dotenv dotenv = Dotenv.load();

    String apiKey = dotenv.get("API_KEY");
    int port = Integer.parseInt(dotenv.get("PORT", "7070"));

    FileRepository repository = new LocalFileRepository(Path.of("storage-data"));

    FileService service = new FileService(repository);
    FileController controller = new FileController(service);


    Javalin app = Javalin.create(config -> {
        config.jetty.multipartConfig.maxFileSize(50, SizeUnit.MB);

        config.bundledPlugins.enableCors(cors -> {
            cors.addRule(it -> {
                it.anyHost();
            });
        });
        config.routes.apiBuilder(() -> {

            before("/api/*", ctx -> {
                if (ctx.method().name().equalsIgnoreCase("OPTIONS")) {
                    return;
                }
                String clientApiKey = ctx.header("X-API-Key");

                if (clientApiKey == null || !clientApiKey.equals(apiKey)) {
                    ctx.status(401);
                    ctx.result("Unauthorized: Invalid or missing api key");
                    ctx.skipRemainingHandlers();
                }
            });


            path("api", () -> {
                path("files", () -> {
                    post(controller::upload);
                    post("/download", controller::download);
                });
            });
        });

        GlobalExceptionHandler.register(config.routes);
    });

    app.start(port);

    System.out.println("GlueChat Storage Api started. App Port: " + port);
}

package org.gluechat.storage;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import org.gluechat.storage.common.GlobalExceptionHandler;
import org.gluechat.storage.common.Jwt;
import org.gluechat.storage.file.*;
import org.gluechat.storage.file.FileController;
import org.gluechat.storage.file.FileRepository;
import org.gluechat.storage.file.FileService;
import org.gluechat.storage.file.LocalFileRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;

import static io.javalin.apibuilder.ApiBuilder.*;


public class Main {
    public static void main(String[] args) throws IOException {
        SecureRandom secureRandom = new SecureRandom();
        Dotenv dotenv = Dotenv.load();
        Jwt jwt = new Jwt(dotenv);

        int port = Integer.parseInt(dotenv.get("PORT", "7070"));

        FileRepository repository = new LocalFileRepository(Path.of("storage-data"));
        FileService service = new FileService(repository,secureRandom);
        FileController controller = new FileController(service);


        Javalin app = Javalin.create(config -> {

            config.http.maxRequestSize = 100L * 1024 * 1024;
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
            config.routes.apiBuilder(() -> {

                before("/storage/*", ctx -> {
                    if (ctx.method().name().equalsIgnoreCase("OPTIONS")) {
                        return;
                    }
                    String clientApiKey = ctx.header("X-API-Key");

                    if (clientApiKey == null || !jwt.verifyToken(clientApiKey)) {
                        ctx.status(401);
                        ctx.result("Unauthorized: Invalid or missing api key");
                        ctx.skipRemainingHandlers();
                    }
                });


                path("storage", () -> {
                        get(ctx -> ctx.result("GlueChat File Server"));
                        post("/download", controller::download);
                        path("files", () -> {
                            post(controller::upload);
                            delete("/delete", controller::delete);
                        });
                });
            });

            GlobalExceptionHandler.register(config.routes);
        });

        app.start(port);

        System.out.println("GlueChat Storage Api started. App Port: " + port);
    }
}



package org.example.storage.common;

import io.javalin.config.RoutesConfig;
import io.javalin.http.HttpResponseException;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static boolean isCausedBySizeLimitExceeded(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current.getMessage() != null && current.getMessage().toLowerCase().contains("exceed")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }


    public static void register(RoutesConfig routes) {
        routes.exception(HttpResponseException.class, (e, ctx) -> {
            ctx.status(e.getStatus()).json(new ServerError(e.getMessage()));
        });



        routes.exception(Exception.class, (e, ctx) -> {

            if (isCausedBySizeLimitExceeded(e)) {
                ctx.status(413);
                ctx.result("Reached size limit (50mb)");
                return;
            }

            logger.error("Unhandled exception during request processing", e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(new ServerError("Something went wrong"));
        });
    }
}

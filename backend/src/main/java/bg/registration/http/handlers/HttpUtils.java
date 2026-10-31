package bg.registration.http.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpUtils {
    public static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset = UTF-8");
        exchange.sendResponseHeaders(status, 0);

        try(var out = exchange.getResponseBody()){
            out.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

}

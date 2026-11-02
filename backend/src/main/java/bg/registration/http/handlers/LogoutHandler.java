package bg.registration.http.handlers;

import bg.registration.service.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class LogoutHandler implements HttpHandler {
    private final SessionManager sessionManager;

    public LogoutHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if (!"POST".equals(method)) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String token = exchange.getRequestHeaders().getFirst("Authorisation");

        if(token == null || sessionManager.getUserId(token).isEmpty()) {
            String json = """
                    {
                    "error":"Unauthorised"
                    }
                    """;
            HttpUtils.sendJson(exchange, 401, json);
            return;
        }

        sessionManager.logout(token);
        String json = """
                    {
                    "message":"Session expired"
                    }
                    """;
        HttpUtils.sendJson(exchange, 200, json);
    }
}

package bg.registration.http.handlers;

import bg.registration.model.User;
import bg.registration.service.SessionManager;
import bg.registration.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class LoginHandler implements HttpHandler {
    private final SessionManager sessionManager;
    private final UserService userService;

    public LoginHandler(SessionManager sessionManager, UserService userService) {
        this.sessionManager = sessionManager;
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if (!"POST".equals(method)) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8);


            ObjectMapper mapper = new ObjectMapper();

            JsonNode node = mapper.readTree(body);

            String email = node.path("email").asText();
            String password = node.path("password").asText();

            Optional<User> user = userService.loginUser(email, password);

            if (user.isPresent()) {

                String token = sessionManager.createSession(user.get().id());

                String json = """
            {
                "token": "%s"
            }
            """.formatted(token);

                HttpUtils.sendJson(exchange, 200, json);

            } else {
                String json = """
            {
                "error":"Invalid email or password"
            }
            """;
                HttpUtils.sendJson(exchange, 401, json);
            }
        } catch (JsonProcessingException e) {
            String json = """
                    "error":"Invalid json"
                    """;
            HttpUtils.sendJson(exchange, 400, json);
        }
    }
}

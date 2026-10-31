package bg.registration.http.handlers;

import bg.registration.model.User;
import bg.registration.repository.UserRepository;
import bg.registration.service.SessionManager;
import bg.registration.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ProfileHandler implements HttpHandler {
    private final SessionManager sessionManager;
    private final UserRepository userRepository;
    private final UserService userService;

    public ProfileHandler(SessionManager sessionManager, UserRepository userRepository, UserService userService) {
        this.sessionManager = sessionManager;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if(!"GET".equals(method) && !"PUT".equals(method)) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String token = exchange.getRequestHeaders().getFirst("Authorisation");
        Optional<Long> userId = sessionManager.getUserId(token);

        if(userId.isEmpty()) {
            String json = """
                    {
                    "error":"Invalid token"
                    }
                    """;
            HttpUtils.sendJson(exchange, 401, json);
            return;
        }

        long id = userId.get();
        Optional<User> findUser = userRepository.findById(id);

        if(findUser.isEmpty()) {
            HttpUtils.sendJson(exchange, 404,  "{\"error:\"User not found\"}");
            return;
        }

        User user = findUser.get();
        if("GET".equals(exchange.getRequestMethod())) {
            try {
                String json = """
                        {
                        "id":"%d",
                        "first_name":"%s",
                        "last_name":"%s",
                        "email":"%s"
                        }
                        """.formatted(user.id(), user.firstName(), user.lastName(), user.email());

                HttpUtils.sendJson(exchange, 200, json);
            } catch (IllegalArgumentException e) {
                String json = """
                        {
                        "error":"Invalid id!"
                        }
                        """;

                HttpUtils.sendJson(exchange, 401, json);

            }
        } else if("PUT".equals(exchange.getRequestMethod())) {
            try{
                String body = new String(exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(body);

                String firstName = node.path("first_name").asText();
                String lastName = node.path("last_name").asText();
                String email = node.path("email").asText();

                boolean changePassword = node.path("change_password").asBoolean(false);
                User updatedUser = userService.updateUser(user.id(), firstName, lastName, email);

                if(changePassword) {
                    String currentPassword = node.path("current_password").asText();
                    String newPassword = node.path("new_password").asText();

                    boolean passChanged = userService.changePassword(updatedUser.id(), currentPassword, newPassword);

                    if(!passChanged) {
                        HttpUtils.sendJson(exchange, 400, "{\"error\":\"Password change failed\"}");
                        return;
                    }

                }

                String json = """
                        {
                        "id":%d
                        "first_name":"%s",
                        "last_name":"%s",
                        "email":"%s"
                        }
                        """.formatted(updatedUser.id(),updatedUser.firstName(),updatedUser.lastName(),updatedUser.email());
                HttpUtils.sendJson(exchange,200,json);

            } catch(IllegalArgumentException e) {
                String json = """
                        {
                        "error":"Invalid data"
                        }
                        """;
                HttpUtils.sendJson(exchange,400,json);
            }
        }
    }
}

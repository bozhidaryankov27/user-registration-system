package bg.registration.http.handlers;

import bg.registration.model.User;
import bg.registration.service.CaptchaService;
import bg.registration.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RegisterHandler implements HttpHandler {
    private final UserService userService;
    private final CaptchaService captchaService;
    public RegisterHandler(UserService userService, CaptchaService captchaService) {
        this.userService = userService;
        this.captchaService = captchaService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if (!"POST".equals(method)) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);


        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode node = mapper.readTree(body);

            String firstName = node.path("first_name").asText();
            String lastName = node.path("last_name").asText();
            String email = node.path("email").asText();
            String password = node.path("password").asText();

            String captchaId = node.path("captcha_id").asText();
            String captchaAnswer = node.path("captcha_answer").asText();

            if(!captchaService.validateCaptcha(captchaId, captchaAnswer)) {
                String json = """
                    {
                    "error":"Invalid CAPTCHA"
                    }
                    """;
                HttpUtils.sendJson(exchange, 400, json);
                return;
            }
            User user = userService.saveUser(firstName, lastName, email, password);

            String json = """
                    {
                    "id":%d,
                    "first_name":"%s",
                    "last_name":"%s",
                    "email":"%s"
                    }
                    """.formatted(user.id(), user.firstName(), user.lastName(), user.email());

            HttpUtils.sendJson(exchange, 201, json);
        } catch (IllegalArgumentException e){
            String json = """
                    {
                    "error":"Invalid data"
                    }
                    """;
            HttpUtils.sendJson(exchange, 400, json);
        } catch (RuntimeException e) {
            e.printStackTrace();
            String json = """
                    {
                    "error":"Internal server error"
                    }
                    """;
            HttpUtils.sendJson(exchange, 500, json);
        } catch (JsonProcessingException e) {
            String json = """
                    {
                    "error":"Invalid json"
                    }
                    """;
            HttpUtils.sendJson(exchange, 400, json);
        }
    }
}

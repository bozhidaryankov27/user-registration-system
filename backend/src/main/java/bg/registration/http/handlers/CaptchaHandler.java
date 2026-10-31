package bg.registration.http.handlers;

import bg.registration.service.CaptchaService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class CaptchaHandler implements HttpHandler {

    private final CaptchaService captchaService;

    public CaptchaHandler(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        if (!"GET".equals(method)) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        CaptchaService.CaptchaChallenge challenge = captchaService.generateCaptcha();

        String json = """
                {
                "id": "%s",
                "question": "%s"
                }
                """.formatted(challenge.id(), challenge.question());

        HttpUtils.sendJson(exchange,200,json);
    }
}
package bg.registration.service;

import bg.registration.http.handlers.RegisterHandler;
import bg.registration.model.User;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

public class RegisterHandlerTest {
    @Test
    void shouldRejectGetRequest() throws IOException {
        UserService userService = mock(UserService.class);
        CaptchaService captchaService = mock(CaptchaService.class);
        HttpExchange exchange = mock(HttpExchange.class);

        RegisterHandler handler = new RegisterHandler(userService, captchaService);

        when(exchange.getRequestMethod()).thenReturn("GET");

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(405, -1);
        verify(exchange).close();
        verifyNoInteractions(userService, captchaService);
    }

    @Test
    void shouldRegisterUser() throws IOException {
        UserService userService = mock(UserService.class);
        CaptchaService captchaService = mock(CaptchaService.class);
        HttpExchange exchange = mock(HttpExchange.class);

        RegisterHandler handler = new RegisterHandler(userService, captchaService);

        String json = """
                {
                    "first_name": "Bozhidar",
                    "last_name": "Yankov",
                    "email": "bojo@gmail.com",
                    "password": "Password!1",
                    "captcha_id": "abc123",
                    "captcha_answer": "8"
                }
                """;

        when(exchange.getRequestMethod()).thenReturn("POST");

        when(exchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
        );

        when(captchaService.validateCaptcha("abc123", "8"))
                .thenReturn(true);

        User user = new User(
                1L, "Bozhidar", "Yankov",
                "bojo@gmail.com", "hashedPassword"
        );

        when(userService.saveUser(
                "Bozhidar", "Yankov", "bojo@gmail.com", "Password!1"
        )).thenReturn(user);

        when(exchange.getResponseBody())
                .thenReturn(new ByteArrayOutputStream());

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        handler.handle(exchange);

        verify(captchaService).validateCaptcha("abc123", "8");

        verify(userService).saveUser(
                "Bozhidar", "Yankov", "bojo@gmail.com", "Password!1"
        );

        verify(exchange).sendResponseHeaders(eq(201), anyLong());
    }
}

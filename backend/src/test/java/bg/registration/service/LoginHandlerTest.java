package bg.registration.service;

import bg.registration.http.handlers.LoginHandler;
import bg.registration.model.User;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class LoginHandlerTest {
    @Test
    void shouldRejectGetRequest() throws IOException {
        UserService userService = mock(UserService.class);
        SessionManager sessionManager = mock(SessionManager.class);
        HttpExchange exchange = mock(HttpExchange.class);

        LoginHandler handler = new LoginHandler(sessionManager, userService);

        when(exchange.getRequestMethod()).thenReturn("GET");

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(405, -1);
        verify(exchange).close();
        verifyNoInteractions(userService, sessionManager);
    }

    @Test
    void shouldLoginSuccessfully() throws IOException {
        UserService userService = mock(UserService.class);
        SessionManager sessionManager = mock(SessionManager.class);
        HttpExchange exchange = mock(HttpExchange.class);

        LoginHandler handler = new LoginHandler(sessionManager, userService);

        String json = """
                {
                    "email": "bojo@gmail.com",
                    "password": "Password!1"
                }
                """;

        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
        );

        User user = new User(
                1L, "Bozhidar", "Yankov",
                "bojo@gmail.com", "hashedPassword"
        );

        when(userService.loginUser("bojo@gmail.com", "Password!1"))
                .thenReturn(Optional.of(user));

        when(sessionManager.createSession(1L)).thenReturn("test-token");

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(userService).loginUser("bojo@gmail.com", "Password!1");
        verify(sessionManager).createSession(1L);
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void shouldNotLogin() throws IOException {
        UserService userService = mock(UserService.class);
        SessionManager sessionManager = mock(SessionManager.class);
        HttpExchange exchange = mock(HttpExchange.class);

        LoginHandler handler = new LoginHandler(sessionManager, userService);

        String json = """
                {
                    "email": "bojo@gmail.com",
                    "password": "Password!1"
                }
                """;

        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
        );

        User user = new User(
                1L, "Bozhidar", "Yankov",
                "bojo@gmail.com", "hashedPassword"
        );

        when(userService.loginUser("bojo@gmail.com", "Password!1"))
                .thenReturn(Optional.empty());

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(userService).loginUser("bojo@gmail.com", "Password!1");
        verify(sessionManager, never()).createSession(1L);
        verify(exchange).sendResponseHeaders(eq(401), anyLong());
    }

    @Test
    void shouldRejectInvalidJson() throws IOException {
        UserService userService = mock(UserService.class);
        SessionManager sessionManager = mock(SessionManager.class);
        HttpExchange exchange = mock(HttpExchange.class);

        LoginHandler handler = new LoginHandler(sessionManager, userService);

        String json = """
                    {
                    "email": "bojo@gmail.com",
                    "password": "Password!1"
                """;

        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
        );

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(userService,never()).loginUser("bojo@gmail.com", "Password!1");
        verify(sessionManager,never()).createSession(1L);
        verify(exchange).sendResponseHeaders(eq(400), anyLong());
    }
}
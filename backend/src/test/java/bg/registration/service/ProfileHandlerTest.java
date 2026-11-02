package bg.registration.service;

import bg.registration.http.handlers.ProfileHandler;
import bg.registration.model.User;
import bg.registration.repository.UserRepository;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class ProfileHandlerTest {
    @Test
    void shouldGetProfile() throws IOException {
        SessionManager sessionManager = mock(SessionManager.class);
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = mock(UserService.class);
        HttpExchange exchange = mock(HttpExchange.class);

        ProfileHandler handler = new ProfileHandler(
                sessionManager, userRepository, userService
        );

        User user = new User(
                1L, "Bozhidar", "Yankov",
                "bojo@gmail.com", "hashedPassword"
        );

        Headers headers = new Headers();
        headers.set("Authorisation", "test-token");

        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(sessionManager.getUserId("test-token"))
                .thenReturn(Optional.of(1L));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(userRepository).findById(1L);
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void shouldUpdateProfile() throws IOException {
        SessionManager sessionManager = mock(SessionManager.class);
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = mock(UserService.class);
        HttpExchange exchange = mock(HttpExchange.class);

        ProfileHandler handler = new ProfileHandler(
                sessionManager, userRepository, userService
        );

        User user = new User(
                1L, "Bozhidar", "Yankov",
                "bojo@gmail.com", "hashedPassword"
        );

        User updatedUser = new User(
                1L, "Georgi", "Filipov",
                "gfilipov@gmail.com", "hashedPassword"
        );

        Headers headers = new Headers();
        headers.set("Authorisation", "test-token");

        String json = """
                {
                    "first_name": "Georgi",
                    "last_name": "Filipov",
                    "email": "gfilipov@gmail.com"
                }
                """;

        when(exchange.getRequestMethod()).thenReturn("PUT");
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(exchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
        );

        when(sessionManager.getUserId("test-token"))
                .thenReturn(Optional.of(1L));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userService.updateUser(
                1L, "Georgi", "Filipov", "gfilipov@gmail.com"
        )).thenReturn(updatedUser);

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(userService).updateUser(
                1L, "Georgi", "Filipov", "gfilipov@gmail.com"
        );
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void shouldUpdateProfileWithPassword() throws IOException {
        SessionManager sessionManager = mock(SessionManager.class);
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = mock(UserService.class);
        HttpExchange exchange = mock(HttpExchange.class);

        ProfileHandler handler = new ProfileHandler(
                sessionManager, userRepository, userService
        );

        User user = new User(
                1L, "Bozhidar", "Yankov",
                "bojo@gmail.com", "hashedPassword"
        );

        Headers headers = new Headers();
        headers.set("Authorisation", "test-token");

        String json = """
                {
                    "first_name": "Georgi",
                    "last_name": "Filipov",
                    "email": "gfilipov@gmail.com",
                    "change_password": true,
                    "current_password": "Password!1",
                    "new_password": "NewPassword!1"
                }
                """;

        when(exchange.getRequestMethod()).thenReturn("PUT");
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(exchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
        );

        when(sessionManager.getUserId("test-token"))
                .thenReturn(Optional.of(1L));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userService.changePassword(
                1L, "Password!1", "NewPassword!1"
        )).thenReturn(true);

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(userService).changePassword(
                1L, "Password!1", "NewPassword!1"
        );

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void shouldRejectInvalidToken() throws IOException {
        SessionManager sessionManager = mock(SessionManager.class);
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = mock(UserService.class);
        HttpExchange exchange = mock(HttpExchange.class);

        ProfileHandler handler = new ProfileHandler(
                sessionManager, userRepository, userService
        );

        Headers headers = new Headers();
        headers.set("Authorisation", "invalid-token");

        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestHeaders()).thenReturn(headers);

        when(sessionManager.getUserId("invalid-token"))
                .thenReturn(Optional.empty());

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(401), anyLong());
        verify(userRepository, never()).findById(anyLong());
    }
}

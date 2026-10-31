package bg.registration.service;

import bg.registration.http.handlers.LogoutHandler;
import bg.registration.http.handlers.RegisterHandler;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class LogoutHandlerTest {
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
    void shouldLogoutSuccessfully() throws IOException {
        SessionManager sessionManager = mock(SessionManager.class);
        HttpExchange exchange = mock(HttpExchange.class);

        LogoutHandler handler = new LogoutHandler(sessionManager);

        Headers requestHeaders = new Headers();
        requestHeaders.set("Authorisation", "test-token");

        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestHeaders()).thenReturn(requestHeaders);

        when(sessionManager.getUserId("test-token"))
                .thenReturn(Optional.of(1L));

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(sessionManager).getUserId("test-token");
        verify(sessionManager).logout("test-token");
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }
}

package bg.registration.service;

import bg.registration.http.handlers.CaptchaHandler;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class CaptchaHandlerTest {
    @Test
    void shouldRejectPostRequest() throws IOException {
        CaptchaService captchaService = mock(CaptchaService.class);
        HttpExchange exchange = mock(HttpExchange.class);

        CaptchaHandler handler = new CaptchaHandler(captchaService);

        when(exchange.getRequestMethod()).thenReturn("POST");

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(405, -1);
        verify(exchange).close();
        verifyNoInteractions(captchaService);
    }

    @Test
    void shouldGenerateCaptcha() throws IOException {
        CaptchaService captchaService = mock(CaptchaService.class);
        HttpExchange exchange = mock(HttpExchange.class);

        CaptchaHandler handler = new CaptchaHandler(captchaService);

        CaptchaService.CaptchaChallenge challenge =
                new CaptchaService.CaptchaChallenge("abc123", "5 + 3 = ?");

        when(exchange.getRequestMethod()).thenReturn("GET");
        when(captchaService.generateCaptcha()).thenReturn(challenge);

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(captchaService).generateCaptcha();
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }
}

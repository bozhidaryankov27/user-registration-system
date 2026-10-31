package bg.registration.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {
    private final SessionManager sessionManager = new SessionManager();


    @Test
    void shouldCreateSession() {
        long userId = 1L;

        String token = sessionManager.createSession(userId);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(userId, sessionManager.getUserId(token).orElseThrow());
    }

    @Test
    void shouldLogout() {
        long userId = 1L;
        String token = sessionManager.createSession(userId);

        sessionManager.logout(token);

        assertTrue(sessionManager.getUserId(token).isEmpty());
    }
}

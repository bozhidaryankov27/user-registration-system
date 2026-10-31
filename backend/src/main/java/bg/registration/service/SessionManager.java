package bg.registration.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    public String createSession(long userId) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, userId);
        return token;
    }

    public Optional<Long> getUserId(String token) {
        if(token == null || token.isBlank()) {
            return Optional.empty();
        }

        Long userId = sessions.get(token);
        return Optional.ofNullable(userId);
    }

    public void logout(String token) {
        if(token == null || token.isBlank()) {
            return;
        }
        sessions.remove(token);
    }
}

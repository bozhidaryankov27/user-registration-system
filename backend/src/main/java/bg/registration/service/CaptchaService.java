package bg.registration.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CaptchaService {
    private final SecureRandom random = new SecureRandom();

    private final Map<String, CaptchaData> captchas =
            new ConcurrentHashMap<>();

    private record CaptchaData(
            int answer,
            Instant expiresAt
    ) {}

    public record CaptchaChallenge(
            String id,
            String question
    ) {}

    public CaptchaChallenge generateCaptcha() {
        int a = random.nextInt(1, 11);
        int b = random.nextInt(1, 11);

        int answer = a + b;

        String question = a + " + " + b + " = " + "?";

        UUID id = UUID.randomUUID();

        Instant expiresAt = Instant.now().plusSeconds(300);

        captchas.put(String.valueOf(id), new CaptchaData(answer, expiresAt));

        return new CaptchaChallenge(String.valueOf(id), question);
    }

    public boolean validateCaptcha(String captchaId, String answer) {
        if(captchaId == null || answer == null) {
             return false;
        }
         CaptchaData captcha = captchas.remove(captchaId);

        if (captcha == null) {
            return false;
        }

         if(Instant.now().isAfter(captcha.expiresAt())) {
             return false;
         }

        try {
            int userAnswer = Integer.parseInt(answer);
            return userAnswer == captcha.answer();
        } catch(NumberFormatException e) {
            return false;
        }
    }

}

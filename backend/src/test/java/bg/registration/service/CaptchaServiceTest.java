package bg.registration.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CaptchaServiceTest {
    private final CaptchaService captchaService = new CaptchaService();

    @Test
    void shouldGenerateCaptcha() {
        var generatedCaptcha = captchaService.generateCaptcha();

        assertNotNull(generatedCaptcha);
        assertNotNull(generatedCaptcha.id());
        assertFalse(generatedCaptcha.question().isBlank());
    }

    @Test
    void shouldValidateCaptcha() {
        var generatedCaptcha = captchaService.generateCaptcha();
        String question = generatedCaptcha.question();
        String[] parts = question.split(" ");

        int firstNumber = Integer.parseInt(parts[0]);
        int secondNumber = Integer.parseInt(parts[2]);

        String answer = String.valueOf(firstNumber + secondNumber);

        assertTrue(captchaService.validateCaptcha(generatedCaptcha.id(), answer));
        assertFalse(captchaService.validateCaptcha(generatedCaptcha.id(), answer));
    }

}

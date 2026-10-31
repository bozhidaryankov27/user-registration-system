package bg.registration.service;

import bg.registration.model.User;
import bg.registration.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private final UserRepository repository = mock(UserRepository.class);
    private final UserService userService = new UserService(repository);

    @Test
    void shouldSaveUser() {
        String firstName = "Bozhidar";
        String lastName = "Yankov";
        String email = "bojo@gmail.com";
        String password = "Bpass!111";

        userService.saveUser(firstName, lastName, email, password);

        verify(repository).save(any(User.class));
    }

    @Test
    void shouldRejectInvalidUser() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.saveUser("", "Yankov", "bojo@gmail.com", "Bpass!111")
        );

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        User existingUser = new User(
                1L, "Bozhidar", "Yankov", "bojo@gmail.com", "Pass1!1!"
        );

        when(repository.findByEmail(existingUser.email())).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () ->
                userService.saveUser("Bozhidar", "Yankov", "bojo@gmail.com", "Bpass!111")
        );
    }

    @Test
    void shouldLoginUser() {
        String password = "Pass1!1!";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User existingUser = new User(
                1L, "Bozhidar", "Yankov", "bojo@gmail.com", hashedPassword
        );

        when(repository.findByEmail(existingUser.email())).thenReturn(Optional.of(existingUser));

        Optional<User> result = userService.loginUser(existingUser.email(), password);

        assertTrue(result.isPresent());
        assertEquals(existingUser, result.get());

    }

    @Test
    void shouldRejectWrongPassword() {
        String password = "Pass1!1!";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User existingUser = new User(
                1L, "Bozhidar", "Yankov", "bojo@gmail.com", hashedPassword
        );

        String testPassword = "OtherPass1!1!";
        Optional<User> result = userService.loginUser(existingUser.email(), testPassword);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpdateUser() {
        User existingUser = new User(
                1L, "Bozhidar", "Yankov", "bojo@gmail.com", "Pass1!1!"
        );

        String firstName = "Ivan";
        String lastName = "Kostov";
        String email = "vankata@abv.bg";

        when(repository.findById(existingUser.id())).thenReturn(Optional.of(existingUser));

        User updatedUser = userService.updateUser(existingUser.id(), firstName, lastName, email);

        assertNotEquals(existingUser, updatedUser);
    }

    @Test
    void shouldChangePassword() {
        String password = "Pass1!1!";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User existingUser = new User(
                1L, "Bozhidar", "Yankov", "bojo@gmail.com", hashedPassword
        );

        when(repository.findById(existingUser.id())).thenReturn(Optional.of(existingUser));
        String newPassword = "NewPass1!1!";

        assertTrue(userService.changePassword(existingUser.id(), password, newPassword));
    }


}

package bg.registration.service;

import bg.registration.model.User;
import bg.registration.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    private String validateName(String name) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name can't be empty!");
        }

        name = name.trim();

        if(name.length() < 2 || name.length() > 50) {
            throw new IllegalArgumentException("Name must be between 2 and 50 characters!");
        }

        if(!name.matches("\\p{L}+(?:['-]\\p{L}+)*")) {
            throw new IllegalArgumentException("Name contains invalid characters!");
        }

        return name;
    }

    private String validateEmail(String email) {
        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is mandatory!");
        }

        email = email.trim();

        if(email.length() > 255) {
            throw new IllegalArgumentException("Email must be up to 255 characters!");
        }

        if(!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email contains invalid characters!");
        }

        return email;
    }

    private String validatePassword(String password) {
        if(password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is mandatory!");
        }

        if(password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Invalid password!");
        }

        return password;
    }

    public boolean changePassword(long id, String currentPass, String newPass) {
        User user = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found!"));
        if(!BCrypt.checkpw(currentPass, user.password())) {
            return false;
        }
        validatePassword(newPass);

        String newHashedPass = BCrypt.hashpw(newPass, BCrypt.gensalt(12));
        User updatedUser = new User(user.id(), user.firstName(), user.lastName(), user.email(), newHashedPass);
        repository.update(updatedUser);
        return true;
    }

    public User saveUser(String firstName, String lastName, String email, String password) {
        firstName = validateName(firstName);
        lastName = validateName(lastName);
        email = validateEmail(email);
        password = validatePassword(password);

        if (repository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        String hashedPass = BCrypt.hashpw(password, BCrypt.gensalt(12));

        User user = new User(0, firstName, lastName, email, hashedPass);

        return repository.save(user);
    }

    public User updateUser(long id,String firstName, String lastName, String email) {
        firstName = validateName(firstName);
        lastName = validateName(lastName);
        email = validateEmail(email);

        User user = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found!"));
        Optional<User> existingUser = repository.findByEmail(email);

        if (existingUser.isPresent() && existingUser.get().id() != id) {
            throw new IllegalArgumentException("Email already exists!");
        }

        User updatedUser = new User(id, firstName, lastName, email, user.password());
        return repository.update(updatedUser);
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> existingUser = repository.findByEmail(email);
        if(existingUser.isEmpty()) {
            return Optional.empty();
        }

        User user = existingUser.get();

        if(password == null || password.isBlank()) {
            return Optional.empty();
        }

        if(!BCrypt.checkpw(password, user.password())) {
            return Optional.empty();
        }

        return existingUser;
    }
}

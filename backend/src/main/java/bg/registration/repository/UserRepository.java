package bg.registration.repository;

import bg.registration.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    User update(User user);
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
}

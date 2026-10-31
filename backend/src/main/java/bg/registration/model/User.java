package bg.registration.model;

public record User(
        long id,
        String firstName,
        String lastName,
        String email,
        String password
){}

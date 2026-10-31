package bg.registration.repository;

import bg.registration.database.DatabaseManager;
import bg.registration.model.User;

import java.sql.*;
import java.util.Optional;

public class SQLUserRepository implements UserRepository{

    @Override
    public User save(User user) {
        String sql = """
                insert into users(first_name, last_name, email, password_hash) 
                    values(?, ?, ?, ?)
                """;

        try(Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.firstName());
            ps.setString(2, user.lastName());
            ps.setString(3, user.email());
            ps.setString(4, user.password());

            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new User(
                            rs.getLong(1),
                            user.firstName(),
                            user.lastName(),
                            user.email(),
                            user.password()
                    );
                }
            }
            throw new SQLException("User Id not found!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User update(User user) {
        String sql = """
                update users 
                set first_name = ?,
                last_name = ?,
                email = ?,
                password_hash = ?
                where id = ?
                """;

        try(Connection connection = DatabaseManager.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1,user.firstName());
            ps.setString(2, user.lastName());
            ps.setString(3, user.email());
            ps.setString(4, user.password());

            ps.setLong(5, user.id());

            int affectedRows = ps.executeUpdate();

            if(affectedRows == 0) {
                throw new RuntimeException("User not found!");
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = """
                select * from users
                 where id = ?
                """;

        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                User user = new User(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("password_hash")
                );

                return Optional.of(user);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by Id!",e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = """
                select * from users
                 where email = ?
                """;

        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                User user = new User(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("password_hash")
                );

                return Optional.of(user);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by email!",e);
        }
    }
}

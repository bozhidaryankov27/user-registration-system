package bg.registration.service;

import bg.registration.database.DatabaseManager;
import bg.registration.model.User;
import bg.registration.repository.SQLUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {
    @Test
    void shouldSaveUser() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        User user = new User(
                1L, "Bozhidar", "Yankov",
                "bojkata@gmail.com", "hashedPassword"
        );

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getLong(1)).thenReturn(5L);

        try (MockedStatic<DatabaseManager> mockedDatabase =
                     mockStatic(DatabaseManager.class)) {

            mockedDatabase.when(DatabaseManager::getConnection)
                    .thenReturn(connection);

            SQLUserRepository repository = new SQLUserRepository();
            User savedUser = repository.save(user);

            verify(ps).executeUpdate();
            verify(ps).getGeneratedKeys();

            assertEquals(5L, savedUser.id());
        }

        verify(ps).setString(1, "Bozhidar");
        verify(ps).setString(2, "Yankov");
        verify(ps).setString(3, "bojkata@gmail.com");
        verify(ps).setString(4, "hashedPassword");
        verify(ps).executeUpdate();
        verify(ps).getGeneratedKeys();
    }

    @Test
    void shouldUpdateUser() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        User user = new User(
                1L, "Bozhidar", "Yankov",
                "bojkata@gmail.com", "hashedPassword"
        );

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseManager> mockedDatabase =
                     mockStatic(DatabaseManager.class)) {

            mockedDatabase.when(DatabaseManager::getConnection)
                    .thenReturn(connection);

            SQLUserRepository repository = new SQLUserRepository();

            User updatedUser = repository.update(user);

            verify(ps).setString(1, "Bozhidar");
            verify(ps).setString(2, "Yankov");
            verify(ps).setString(3, "bojkata@gmail.com");
            verify(ps).setString(4, "hashedPassword");
            verify(ps).setLong(5, 1L);
            verify(ps).executeUpdate();

            assertEquals(user, updatedUser);
        }
    }

    @Test
    void shouldFindUserById() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        User user = new User(
                1L, "Bozhidar", "Yankov",
                "bojkata@gmail.com", "hashedPassword"
        );

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("first_name")).thenReturn("Bozhidar");
        when(rs.getString("last_name")).thenReturn("Yankov");
        when(rs.getString("email")).thenReturn("bojkata@gmail.com");
        when(rs.getString("password_hash")).thenReturn("hashedPassword");

        try (MockedStatic<DatabaseManager> mockedDatabase =
                     mockStatic(DatabaseManager.class)) {

            mockedDatabase.when(DatabaseManager::getConnection)
                    .thenReturn(connection);

            SQLUserRepository repository = new SQLUserRepository();

            Optional<User> foundUser = repository.findById(1L);

            assertTrue(foundUser.isPresent());
            assertEquals(user, foundUser.get());

            verify(ps).setLong(1, 1L);
            verify(ps).executeQuery();
        }
    }

    @Test
    void shouldFindUserByEmail() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        User user = new User(
                1L, "Bozhidar", "Yankov",
                "bojkata@gmail.com", "hashedPassword"
        );

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("first_name")).thenReturn("Bozhidar");
        when(rs.getString("last_name")).thenReturn("Yankov");
        when(rs.getString("email")).thenReturn("bojkata@gmail.com");
        when(rs.getString("password_hash")).thenReturn("hashedPassword");

        try (MockedStatic<DatabaseManager> mockedDatabase =
                     mockStatic(DatabaseManager.class)) {

            mockedDatabase.when(DatabaseManager::getConnection)
                    .thenReturn(connection);

            SQLUserRepository repository = new SQLUserRepository();

            Optional<User> foundUser = repository.findByEmail("bojkata@gmail.com");

            assertTrue(foundUser.isPresent());
            assertEquals(user, foundUser.get());

            verify(ps).setString(1, "bojkata@gmail.com");
            verify(ps).executeQuery();
        }
    }

    @Test
    void shouldReturnEmptyWhenUserIdNotFound() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DatabaseManager> mockedDatabase =
                     mockStatic(DatabaseManager.class)) {

            mockedDatabase.when(DatabaseManager::getConnection)
                    .thenReturn(connection);

            SQLUserRepository repository = new SQLUserRepository();

            Optional<User> result = repository.findById(50L);

            assertTrue(result.isEmpty());

            verify(ps).setLong(1, 50L);
            verify(ps).executeQuery();
        }
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DatabaseManager> mockedDatabase =
                     mockStatic(DatabaseManager.class)) {

            mockedDatabase.when(DatabaseManager::getConnection)
                    .thenReturn(connection);

            SQLUserRepository repository = new SQLUserRepository();

            Optional<User> result = repository.findByEmail("bojidaryankov2006@gmail.com");

            assertTrue(result.isEmpty());

            verify(ps).setString(1, "bojidaryankov2006@gmail.com");
            verify(ps).executeQuery();
        }
    }
}

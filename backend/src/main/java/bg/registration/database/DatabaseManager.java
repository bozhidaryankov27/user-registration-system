package bg.registration.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String url = "jdbc:mysql://localhost:3306/user_registration";
    private static final String user = "db_user";
    private static final String pass = "db_pass";
    private DatabaseManager(){}

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, user, pass);
    }
}

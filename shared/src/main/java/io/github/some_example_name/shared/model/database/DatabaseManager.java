package io.github.some_example_name.shared.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    //"C:\Users\Dotcom\Desktop\Project+phase3 - Copy (3)\core\db"
    private static final String DB_PATH = "core/db/mydatabase.db";
    private static Connection connection;

    // اتصال به دیتابیس
    public static void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            System.out.println("✅ Connected to SQLite database.");
        }
    }

    // گرفتن Connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    // بستن اتصال
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 SQLite connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

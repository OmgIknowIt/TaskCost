package home.work.taskcost.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String dbName = "myDB";
    private static final String db = "jdbc:hsqldb:file:src/main/resources/db/" + dbName;
    private static final String user = "SA";
    private static final String password = "";
    private static final String path = "src/main/resources/sql/tables.sql";
    private static Connection conn;

    public static void openConnection() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            conn = DriverManager.getConnection(db, user, password);
            new DbCreation(conn, path).create();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return conn;
    }
}

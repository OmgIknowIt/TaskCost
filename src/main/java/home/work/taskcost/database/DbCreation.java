package home.work.taskcost.database;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DbCreation {

    private Connection conn;
    private String pathToSql;

    public DbCreation(Connection conn, String pathToSql) {
        this.conn = conn;
        this.pathToSql = pathToSql;
    }

    public void create() throws IOException, SQLException {
        String create = readToString(pathToSql);
        conn.createStatement().executeUpdate(create);
    }

    private String readToString(String fileName) throws IOException {
        File file = new File(fileName);
        return FileUtils.readFileToString(file, "utf-8");
    }
}

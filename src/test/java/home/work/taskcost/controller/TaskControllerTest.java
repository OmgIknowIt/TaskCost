package home.work.taskcost.controller;

import home.work.taskcost.database.DbCreation;
import home.work.taskcost.entities.Operation;
import home.work.taskcost.entities.Status;
import home.work.taskcost.entities.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TaskControllerTest {

    Connection conn;

    @Before
    public void setUp() throws SQLException, IOException, ClassNotFoundException {
        String dbName = "myTestDB";
        String db = "jdbc:hsqldb:mem:" + dbName + ";shutdown=true";
        String user = "SA";
        String password = "";
        String path = "src/main/resources/sql/tables.sql";
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        this.conn = DriverManager.getConnection(db, user, password);
        new DbCreation(conn, path).create();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    private Task getStockTask() {
        Task t = new Task();
        t.setDescription("ABCDEF");
        t.setTaskStatus(Status.Project);
        t.setTaskFinished(null);
        return t;
    }

    @Test
    public void createNew() throws SQLException {
        TaskController controller = new TaskController(conn);
        Task t = getStockTask();
        Long id = controller.createNew(t);
        assertEquals(Long.valueOf(0), id);
    }

    @Test
    public void getById() throws SQLException {
        TaskController controller = new TaskController(conn);
        controller.createNew(getStockTask());
        Task t = controller.getById(0L);
        assertEquals(Long.valueOf(0), t.getId());
        assertEquals("ABCDEF", t.getDescription());
        assertEquals("0.00", t.getCost().toString());
        assertEquals(Status.Project, t.getTaskStatus());
        assertNull(t.getTaskFinished());
    }

    @Test
    public void update() throws SQLException {
        TaskController controller = new TaskController(conn);
        controller.createNew(getStockTask());
        Task t = controller.getById(0L);
        t.setCost(BigDecimal.valueOf(100));
        t.setTaskFinished(LocalDate.now());
        t.setTaskStatus(Status.Done);
        assertEquals(1, controller.update(t));
        t = controller.getById(0L);
        assertEquals("100.00", t.getCost().toString());
        assertEquals(LocalDate.now(), t.getTaskFinished());
        assertEquals(LocalDate.now(), t.getTaskFinished());
        assertEquals(Status.Done, t.getTaskStatus());
    }

    @Test
    public void getAll() {
        TaskController controller = new TaskController(conn);
        controller.createNew(getStockTask());
        Set<Task> tasks = controller.getAll();
        assertEquals(1, tasks.size());
        for (Task t : tasks) {
            assertEquals(Long.valueOf(0), t.getId());
            assertEquals("ABCDEF", t.getDescription());
        }
    }

    @Test
    public void checkOverallStatus() throws SQLException {
        TaskController controller = new TaskController(conn);
        controller.createNew(getStockTask());
        assertEquals(Status.Project, controller.checkOverallStatus(0L));

        OperationController operationController = new OperationController(conn);
        Operation o = new Operation();
        o.setTaskId(0L);
        o.setPlannedQuantity(0);
        o.setActualQuantity(0);
        o.setDescription("QWERTY");
        o.setPrice(BigDecimal.valueOf(0));
        o.setCost(BigDecimal.valueOf(0));
        o.setOperationStatus(Status.Project);
        o.setId(operationController.createNew(o));
        assertEquals(Status.InProgress, controller.checkOverallStatus(0L));
        o.setOperationStatus(Status.Done);
        operationController.update(o);
        assertEquals(Status.Done, controller.checkOverallStatus(0L));
    }
}
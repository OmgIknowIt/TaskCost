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
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class OperationControllerTest {

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
        insertData();
    }

    private void insertData() {
        TaskController taskController = new TaskController(conn);
        Task t = new Task();
        t.setDescription("ABCDEF");
        t.setTaskStatus(Status.Project);
        taskController.createNew(t);
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    public Operation getStockOperation() {
        Operation o = new Operation();
        o.setTaskId(0L);
        o.setPlannedQuantity(10);
        o.setActualQuantity(0);
        o.setDescription("QWERTY");
        o.setPrice(BigDecimal.valueOf(20));
        o.setCost(o.getPrice().multiply(BigDecimal.valueOf(o.getPlannedQuantity())));
        o.setOperationStatus(Status.Project);
        return o;
    }

    @Test
    public void createNew() throws SQLException {
        OperationController operationController = new OperationController(conn);
        Long id = operationController.createNew(getStockOperation());
        assertEquals(Long.valueOf(0), id);
    }

    @Test
    public void getById() throws SQLException {
        OperationController operationController = new OperationController(conn);
        Long id = operationController.createNew(getStockOperation());
        Operation op = operationController.getById(id);
        assertEquals(id, op.getId());
        assertEquals(Long.valueOf(0), op.getTaskId());
        assertEquals("QWERTY", op.getDescription());
        assertEquals("20.00", op.getPrice().toString());
        assertEquals(Integer.valueOf(10), op.getPlannedQuantity());
    }

    @Test
    public void update() throws SQLException {
        OperationController operationController = new OperationController(conn);
        operationController.createNew(getStockOperation());
        Operation operation = operationController.getById(0L);
        operation.setActualQuantity(40);
        operation.setOperationStatus(Status.Done);
        operationController.update(operation);

        operation = operationController.getById(0L);
        BigDecimal cost = getStockOperation().getPrice().multiply(BigDecimal.valueOf(operation.getActualQuantity()));
        assertEquals(getStockOperation().getPrice().multiply(BigDecimal.valueOf(40)), cost);
        assertEquals(Status.Done, operation.getOperationStatus());
    }

    @Test
    public void getAll() throws SQLException {
        OperationController operationController = new OperationController(conn);
        operationController.createNew(getStockOperation());
        operationController.createNew(getStockOperation());
        operationController.createNew(getStockOperation());

        Set<Operation> operations = operationController.getAll();
        assertEquals(3, operations.size());
    }
}
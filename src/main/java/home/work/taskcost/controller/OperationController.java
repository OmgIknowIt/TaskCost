package home.work.taskcost.controller;

import home.work.taskcost.entities.Operation;
import home.work.taskcost.entities.Status;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class OperationController implements Controller<Operation> {
    private final Connection connection;

    public OperationController(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Long createNew(Operation operation) throws SQLException {
        String sqlInsert = "INSERT INTO TASK_OPERATION (TASK_ID,DESCRIPTION,PLANNED_QUANTITY,ACTUAL_QUANTITY,PRICE,COST,OPERATION_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Long id = null;
        PreparedStatement pstmt = connection.prepareStatement(sqlInsert, new String[]{"OPERATION_ID"});
        pstmt.setLong(1, operation.getTaskId());
        pstmt.setString(2, operation.getDescription());
        pstmt.setInt(3, operation.getPlannedQuantity());
        pstmt.setInt(4, operation.getActualQuantity());
        pstmt.setBigDecimal(5, operation.getPrice());
        pstmt.setBigDecimal(6, operation.getPrice().multiply(BigDecimal.valueOf(operation.getPlannedQuantity())));
        pstmt.setString(7, String.valueOf(operation.getOperationStatus()));
        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        return id;
    }

    @Override
    public Operation getById(Long taskId) {
        String sqlSelect = "SELECT * FROM TASK_OPERATION WHERE OPERATION_ID = ?";
        Operation operation = new Operation();
        try {
            PreparedStatement pstmt = connection.prepareStatement(sqlSelect);
            pstmt.setLong(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                operation.setId(rs.getLong("OPERATION_ID"));
                operation.setTaskId(rs.getLong("TASK_ID"));
                operation.setDescription(rs.getString("DESCRIPTION"));
                operation.setPlannedQuantity(rs.getInt("PLANNED_QUANTITY"));
                operation.setActualQuantity(rs.getInt("ACTUAL_QUANTITY"));
                operation.setPrice(rs.getBigDecimal("PRICE"));
                operation.setOperationStatus(Status.valueOf(rs.getString("OPERATION_STATUS")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return operation;
    }

    @Override
    public int update(Operation o) throws SQLException {
        String sqlUpdate = "UPDATE TASK_OPERATION SET ACTUAL_QUANTITY = ?, COST = ?, OPERATION_STATUS = ? WHERE OPERATION_ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sqlUpdate);
        pstmt.setInt(1, o.getActualQuantity());
        BigDecimal cost = o.getPrice().multiply(BigDecimal.valueOf(o.getActualQuantity()));
        pstmt.setBigDecimal(2, cost);
        pstmt.setString(3, String.valueOf(o.getOperationStatus()));
        pstmt.setLong(4, o.getId());
        return pstmt.executeUpdate();
    }

    @Override
    public Set<Operation> getAll() {
        Set<Operation> operations = new HashSet<>();
        String sqlSelect = "SELECT * FROM TASK_OPERATION";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sqlSelect);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Operation operation = new Operation();
                operation.setId(rs.getLong("OPERATION_ID"));
                operation.setTaskId(rs.getLong("TASK_ID"));
                operation.setDescription(rs.getString("DESCRIPTION"));
                operation.setPlannedQuantity(rs.getInt("PLANNED_QUANTITY"));
                operation.setActualQuantity(rs.getInt("ACTUAL_QUANTITY"));
                operation.setPrice(rs.getBigDecimal("PRICE"));
                operation.setCost(rs.getBigDecimal("COST"));
                operation.setOperationStatus(Status.valueOf(rs.getString("OPERATION_STATUS")));
                operations.add(operation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return operations;
    }
}

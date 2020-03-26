package home.work.taskcost.controller;

import home.work.taskcost.entities.Operation;
import home.work.taskcost.entities.Status;
import home.work.taskcost.entities.Task;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class TaskController implements Controller<Task> {
    private final Connection connection;

    public TaskController(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Long createNew(Task task) {
        Long id = null;
        String sqlCommand = "INSERT INTO TASK (DESCRIPTION,COST,TASK_STATUS,TASK_FINISHED) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sqlCommand, new String[]{"TASK_ID"});
            pstmt.setString(1, task.getDescription());
            pstmt.setBigDecimal(2, BigDecimal.valueOf(0));
            pstmt.setString(3, String.valueOf(task.getTaskStatus()));
            pstmt.setDate(4, null);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public Task getById(Long taskId) {
        String sqlCommand = "SELECT * FROM TASK WHERE TASK_ID = ?";
        Task task = new Task();
        try {
            PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
            pstmt.setLong(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                task.setId(rs.getLong("TASK_ID"));
                task.setDescription(rs.getString("DESCRIPTION"));
                task.setCost(rs.getBigDecimal("COST"));
                task.setTaskStatus(Status.valueOf(rs.getString("TASK_STATUS")));
                if (rs.getDate("TASK_FINISHED") != null) {
                    task.setTaskFinished(rs.getDate("TASK_FINISHED").toLocalDate());
                } else {
                    task.setTaskFinished(null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return task;
    }

    @Override
    public int update(Task t) throws SQLException {
        String sqlUpdate = "UPDATE TASK SET COST = ?, TASK_STATUS = ?, TASK_FINISHED = ? WHERE TASK_ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sqlUpdate);
        pstmt.setBigDecimal(1, t.getCost());
        pstmt.setString(2, String.valueOf(t.getTaskStatus()));
        if (t.getTaskFinished() != null) {
            pstmt.setDate(3, Date.valueOf(t.getTaskFinished()));
        } else {
            pstmt.setDate(3, null);
        }
        pstmt.setLong(4, t.getId());
        return pstmt.executeUpdate();
    }

    @Override
    public Set<Task> getAll() {
        Set<Task> tasks = new HashSet<>();
        String sqlSelect = "SELECT * FROM TASK";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sqlSelect);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getLong("TASK_ID"));
                task.setDescription(rs.getString("DESCRIPTION"));
                task.setCost(rs.getBigDecimal("COST"));
                task.setTaskStatus(Status.valueOf(rs.getString("TASK_STATUS")));
                Date date = rs.getDate("TASK_FINISHED");
                if (date != null) {
                    task.setTaskFinished(date.toLocalDate());
                } else {
                    task.setTaskFinished(null);
                }
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public Status checkOverallStatus(Long taskId) {
        Set<Operation> operations = new OperationController(connection).getAll();
        if (operations.isEmpty()) return Status.Project;
        for (Operation o : operations) {
            if (o.getTaskId().equals(taskId)) {
                if (!o.getOperationStatus().equals(Status.Done)) {
                    return Status.InProgress;
                }
            }
        }
        return Status.Done;
    }
}

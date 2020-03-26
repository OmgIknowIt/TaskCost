package home.work.taskcost.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Task {
    private Long id;
    private String description;
    private BigDecimal cost;
    private Status taskStatus;
    private LocalDate taskFinished;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    public LocalDate getTaskFinished() {
        return taskFinished;
    }

    public void setTaskFinished(LocalDate taskFinished) {
        this.taskFinished = taskFinished;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", cost=" + cost +
                ", taskStatus=" + taskStatus +
                ", taskFinished=" + taskFinished +
                '}';
    }
}

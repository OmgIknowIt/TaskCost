package home.work.taskcost.entities;

import java.math.BigDecimal;

public class Operation {
    private Long id;
    private Long taskId;
    private String description;
    private Integer plannedQuantity;
    private Integer actualQuantity;
    private BigDecimal price;
    private BigDecimal cost;
    private Status operationStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(Integer plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    public Integer getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(Integer actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Status getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(Status operationStatus) {
        this.operationStatus = operationStatus;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", description='" + description + '\'' +
                ", plannedQuantity=" + plannedQuantity +
                ", actualQuantity=" + actualQuantity +
                ", price=" + price +
                ", cost=" + cost +
                ", operationStatus=" + operationStatus +
                '}';
    }
}

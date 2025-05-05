package domain;

import java.io.Serializable;

public class Task extends Entity implements Serializable {
    private String description;
    private int employeeId;
    private TaskStatus status;

    public Task(int id, String description, int employeeId, TaskStatus status) {
        super(id);
        this.description = description;
        this.employeeId = employeeId;
        this.status = TaskStatus.PENDING;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
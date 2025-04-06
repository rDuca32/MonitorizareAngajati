package domain;

public class Task extends Entity {
    private String description;
    private int employeeId;
    private TaskStatus status;
    private static int nextTaskId = 1;

    public Task(String description, int employeeId) {
        super(nextTaskId++);
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
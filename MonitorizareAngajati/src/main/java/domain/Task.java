package domain;

public class Task {
    private int taskId;
    private String description;
    private int employeeId;
    private TaskStatus status;

    private static int nextTaskId = 1;

    public Task(String description, int employeeId) {
        this.taskId = nextTaskId++;
        this.description = description;
        this.employeeId = employeeId;
        this.status = TaskStatus.PENDING;
    }

    public int getTaskId() {
        return taskId;
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
}
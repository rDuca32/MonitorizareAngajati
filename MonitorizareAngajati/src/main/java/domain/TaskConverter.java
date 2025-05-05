package domain;

public class TaskConverter extends EntityConverter<Task> {
    @Override
    public String toString(Task task) {
        return task.getId() + ";" + task.getDescription() + ";" + task.getEmployeeId() + ";" + task.getStatus();
    }

    @Override
    public Task fromString(String string) {
        String[] parts = string.split(";");
        int id = Integer.parseInt(parts[0]);
        String description = parts[1];
        int employeeId = Integer.parseInt(parts[2]);
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        return new Task(id, description, employeeId, status);
    }
}

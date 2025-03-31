package domain;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Employee extends User {
    private int employeeId;
    private String name;
    private Time arrivalTime;
    private boolean isPresent;
    private List<Task> tasks;

    public Employee(String username, String password, UserType type, int employeeId, String name) {
        super(username, password, type);
        this.employeeId = employeeId;
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void markPresence() {
        LocalTime now = LocalTime.now();
        this.arrivalTime = new Time(now.getHour(), now.getMinute());
        this.isPresent = true;
    }

    public List<Task> viewTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void logout() {
        this.isPresent = false;
    }
}

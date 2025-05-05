package domain;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Employee extends User {
    private Time arrivalTime;
    private boolean isPresent;
    private List<Task> tasks;

    public Employee(int employeeId, String username, String password) {
        super(employeeId, username, password, UserType.EMPLOYEE);
        this.tasks = new ArrayList<>();
        this.isPresent = false;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void markPresence() {
        LocalTime now = LocalTime.now();
        this.arrivalTime = new Time(now.getHour(), now.getMinute());
        this.isPresent = true;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void logout() {
        this.isPresent = false;
    }
}
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

    public void logout() {
        this.isPresent = false;
    }
}
package domain;

import java.util.ArrayList;
import java.util.List;

public class Manager extends User {
    private int managerId;
    private String name;
    private List<Employee> employees;

    public Manager(String username, String password, UserType type, int managerId, String name) {
        super(username, password, type);
        this.managerId = managerId;
        this.name = name;
        this.employees = new ArrayList<>();
    }

    public int getManagerId() {
        return managerId;
    }

    public String getName() {
        return name;
    }

    public List<Employee> viewPresentEmployees() {
        List<Employee> presentEmployees = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.isPresent()) {
                presentEmployees.add(employee);
            }
        }
        return presentEmployees;
    }
}
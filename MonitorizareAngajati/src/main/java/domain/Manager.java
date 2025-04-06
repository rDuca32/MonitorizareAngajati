package domain;

import java.util.ArrayList;
import java.util.List;

public class Manager extends User {
    private String name;
    private List<Employee> employees;

    public Manager(int managerId, String username, String password, String name) {
        super(managerId, username, password, UserType.MANAGER);
        this.name = name;
        this.employees = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Employee> getEmployees() {
        return employees;
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

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }

    public void receiveLogoutNotification(Employee employee) {
        System.out.println("Employee " + employee.getName() + " has logged out.");
    }
}
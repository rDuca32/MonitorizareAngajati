package domain;

import java.util.ArrayList;
import java.util.List;

public class Manager extends User {
    private List<Employee> employees;

    public Manager(int managerId, String username, String password) {
        super(managerId, username, password, UserType.MANAGER);
        this.employees = new ArrayList<>();
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

    public void receiveLogoutNotification(Employee employee) {
        System.out.println("Employee " + employee.getUsername()+ " has logged out.");
    }
}
package domain;

public class Employee extends User {

    public Employee(int employeeId, String username, String password) {
        super(employeeId, username, password, UserType.EMPLOYEE);
    }
}
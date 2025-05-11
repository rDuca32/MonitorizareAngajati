package domain;

public class Manager extends User {

    public Manager(int managerId, String username, String password) {
        super(managerId, username, password, UserType.MANAGER);
    }
}
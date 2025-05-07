package domain;

public abstract class User extends Entity {
    private String username;
    private String password;
    private UserType type;

    public User(int id, String username, String password, UserType type) {
        super(id);
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public boolean isManager() {
        return this.type == UserType.MANAGER;
    }

    public boolean isEmployee() {
        return this.type == UserType.EMPLOYEE;
    }
}
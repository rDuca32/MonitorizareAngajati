package service;

import domain.*;
import repository.SQLUserRepository;
import java.util.Objects;

public class LoginService {
    private final SQLUserRepository userRepository;
    private User currentUser;

    public LoginService(SQLUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) throws LoginException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new LoginException("Username not found");
        }

        if (!Objects.equals(user.getPassword(), password)) {
            throw new LoginException("Invalid password");
        }

        this.currentUser = user;
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isManagerLoggedIn() {
        return currentUser != null && currentUser.isManager();
    }

    public boolean isEmployeeLoggedIn() {
        return currentUser != null && currentUser.isEmployee();
    }
}
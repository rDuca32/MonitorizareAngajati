package repository;

import domain.*;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLUserRepository extends MemoryRepository<User> implements AutoCloseable {
    private static final String JDBC_URL = "jdbc:sqlite:C:/Users/rauld/Documents/GitHub/MonitorizareAngajati/MonitorizareAngajati/monitorizare_angajati.db?journal_mode=WAL";
    private Connection connection;

    public SQLUserRepository() {

        openConnection();
        createSchema();
        loadData();

        if (collection.isEmpty())
            InitialValues();
    }

    private void createSchema() {
        try {
            try (final Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("create table if not exists users (id int primary key, username varchar(50), password varchar(50), role varchar(50))");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] createSchema : " + e.getMessage());
        }
    }

    private void openConnection() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(JDBC_URL);
            if (connection == null || connection.isClosed()) {
                connection = ds.getConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database: " + e.getMessage());
        }
    }

    private void loadData() {
        try{
            PreparedStatement statement = connection.prepareStatement("select * from users");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");

                User user;
                if (role.equals("manager")) {
                    user = new Manager(id, username, password);
                } else {
                    user = new Employee(id, username, password);
                }
                collection.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(User user) throws RepositoryException {
        try {
            String sql = "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setString(4, user instanceof Manager ? "manager" : "employee");
            statement.executeUpdate();
            collection.add(user);
        } catch (SQLException e) {
            throw new RepositoryException("Error adding user: " + e.getMessage());
        }
    }

    public void remove(User user) throws RepositoryException {
        try {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, user.getId());
            statement.executeUpdate();
            collection.remove(user);
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting user: " + e.getMessage());
        }
    }

    public void update(User user) throws RepositoryException {
        try {
            String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user instanceof Manager ? "manager" : "employee");
            statement.setInt(4, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating user: " + e.getMessage());
        }
    }

    public User findByUsername(String username) {
        for (User user : collection) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void InitialValues(){
        List<User> users = new ArrayList<>();
        String[] usernames = {"m", "e1", "e2", "e3"};
        String[] passwords = {"m123", "e123", "e123", "e123"};

        for (int i = 0; i < usernames.length; i++) {
            User user;
            if (i == 0) {
                user = new Manager(i + 1, usernames[i], passwords[i]);
            } else {
                user = new Employee(i + 1, usernames[i], passwords[i]);
            }
            users.add(user);
        }

        for (User user : users) {
            try {
                add(user);
            } catch (RepositoryException e) {
                System.err.println("Error adding initial user: " + e.getMessage());
            }
        }

        System.out.println("Initial users added to the database.");
    }

    public Integer getEmployeeIdByName(String employeeName) {
        try {
            String sql = "SELECT id FROM users WHERE username = ? AND role = 'employee'";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, employeeName);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return null;
        } catch (SQLException e) {
            System.err.println("[ERROR] getEmployeeIdByName: " + e.getMessage());
            return null;
        }
    }
}
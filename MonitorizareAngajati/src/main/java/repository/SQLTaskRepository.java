package repository;

import domain.*;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLTaskRepository extends MemoryRepository<Task> implements AutoCloseable {
    private static final String JDBC_URL = "jdbc:sqlite:/home/ducaraul/Documents/GitHub/MonitorizareAngajati/MonitorizareAngajati/monitorizare_angajati.db";
    private Connection connection;

    public SQLTaskRepository() {
        openConnection();
        createSchema();
        loadData();

        if (collection.isEmpty())
            InitialValues();
    }

    private void loadData() {
        try{
            PreparedStatement statement = connection.prepareStatement("select * from tasks");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                int employeeId = rs.getInt("employeeId");
                String status = rs.getString("status");
                Task task = new Task(description, employeeId);
                task.setStatus(TaskStatus.valueOf(status));
                task.setId(id);
                collection.add(task);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSchema() {
        try {
            try (final Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("create table if not exists tasks (id int primary key, description varchar(50), employeeId int, status varchar(50) foreign key(employeeId) references users(id))");
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
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

    public void add(Task task) throws RepositoryException {
        try {
            String sql = "INSERT INTO tasks (id, description, employeeId, status) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, task.getId());
            statement.setString(2, task.getDescription());
            statement.setInt(3, task.getEmployeeId());
            statement.setString(4, task.getStatus().toString());
            statement.executeUpdate();
            collection.add(task);

        } catch (SQLException e) {
            throw new RepositoryException("Error adding task: " + e.getMessage());
        }
    }

    public void remove(Task task) throws RepositoryException {
        try {
            String sql = "DELETE FROM tasks WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, task.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting user: " + e.getMessage());
        }
    }

    public void update(Task task) throws RepositoryException {
        try {
            String sql = "UPDATE tasks SET description = ?, employeeId = ?, status = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, task.getId());
            statement.setString(2, task.getDescription());
            statement.setInt(3, task.getEmployeeId());
            statement.setString(4, task.getStatus().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    private void InitialValues() {
        List<Task> tasks = new ArrayList<>();
    }

}
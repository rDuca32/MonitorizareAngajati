package repository;

import domain.Task;
import domain.TaskStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLTaskRepository extends MemoryRepository<Task> implements AutoCloseable {
    // TODO

    private static final String JDBC_URL = "jdbc:sqlite:C:/Users/rauld/Documents/GitHub/MonitorizareAngajati/MonitorizareAngajati/monitorizare_angajati.db";
    private Connection connection;

    public SQLTaskRepository() {

    }

    private void loadData() {
    }

    private void createSchema() {
        
    }

    private void openConnection() {
        
    }

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
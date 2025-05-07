package repository;

import domain.Entity;
import domain.EntityConverter;
import java.io.*;

public class TextFileRepository<T extends Entity> extends AbstractFileRepository<T> {

    protected EntityConverter<T> converter;

    public TextFileRepository(String fileName, EntityConverter<T> converter) {
        super(fileName);
        this.converter = converter;
        try {
            loadFile();
        } catch (RepositoryException e) {
            throw new RuntimeException("Failed to load file " + fileName, e);
        }
    }

    @Override
    protected void saveFile() throws RepositoryException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (var item : this.collection) {
                String asString = converter.toString((T) item);
                writer.write(asString);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RepositoryException("Error saving file " + fileName, e);
        }
    }

    @Override
    protected void loadFile() throws RepositoryException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();
            while (line != null) {
                collection.add(converter.fromString(line));
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException e) {
            throw new RepositoryException("Error loading file " + fileName, e);
        }
    }
}

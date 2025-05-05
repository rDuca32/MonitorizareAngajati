package repository;

import domain.Entity;
import domain.EntityConverter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextFileRepository<T extends Entity> extends AbstractFileRepository<T> {

    protected EntityConverter<T> converter;

    public TextFileRepository(String fileName, EntityConverter<T> converter) {
        super(fileName);
        this.converter = converter;
        try {
            loadFile();
        } catch (RepositoryException e) {
            throw new RuntimeException("Nu s-a reusit incarcarea datelor din fisier.", e);
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
            throw new RepositoryException("Eroare la salvarea in fisierul text.", e);
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
            System.out.println("Fisierul nu a fost gasit. Initializare un fisier .txt gol.");
        } catch (IOException e) {
            throw new RepositoryException("Eroare la citirea din fisierul text.", e);
        }
    }
}

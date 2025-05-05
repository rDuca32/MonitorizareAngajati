package repository;

import domain.Entity;

public abstract class AbstractFileRepository<T extends Entity> extends MemoryRepository<T> {
    protected String fileName;

    public AbstractFileRepository(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void add(T elem) throws RepositoryException {
        super.add(elem);
        saveFile();
    }

    @Override
    public void remove(T elem) throws RepositoryException {
        super.remove(elem);
        saveFile();
    }

    @Override
    public void update(T elem) throws RepositoryException {
        super.update(elem);
        saveFile();
    }

    protected abstract void saveFile() throws RepositoryException;

    protected abstract void loadFile() throws RepositoryException;
}

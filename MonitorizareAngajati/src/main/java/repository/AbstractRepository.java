package repository;

import domain.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractRepository <T extends Entity> implements Iterable<T> {
    protected List<T> collection = new ArrayList<>();

    public abstract void add(T item) throws RepositoryException;
    public abstract void remove(T item) throws RepositoryException;
    public abstract void update(T item) throws RepositoryException;
    public abstract T find(int id);
    public abstract int size();
    public abstract Collection<T> getAll();

    @Override
    public Iterator<T> iterator(){
        return collection.iterator();
    }
}

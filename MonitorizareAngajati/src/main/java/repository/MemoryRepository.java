package repository;

import domain.Entity;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryRepository <T extends Entity> extends AbstractRepository<T> {
    @Override
    public void add(T elem) throws RepositoryException {
        if (find(elem.getId()) != null)
            throw new DuplicateIDException("Exista deja - " + elem.getId());
        collection.add(elem);
    }

    @Override
    public void remove(T elem) throws RepositoryException {
        if(!collection.remove(find(elem.getId())))
            throw new RepositoryException("Nu a fost gasit - " + elem.getId());
    }

    @Override
    public void update(T elem) throws RepositoryException {
        T existingItem = find(elem.getId());
        if (existingItem == null) {
            throw new RepositoryException("Nu a fost gasit - " + elem.getId());
        }
        collection.remove(existingItem);
        collection.add(elem);
    }

    @Override
    public T find(int id){
        for(T elem : collection){
            if (elem.getId() == id)
                return elem;
        }
        return null;
    }

    @Override
    public int size() {
        return collection.size();
    }

    @Override
    public Collection<T> getAll() {
        return new ArrayList<>(collection);
    }
}
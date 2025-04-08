package repository;

import model.HasId;

import java.util.Collection;

public interface IRepo<ID, T extends HasId<ID>> {
    int size();
    void add(T elem);
    void delete(T elem);
    void update(ID id, T elem);
    T findById(ID id);
    Iterable<T> findAll();
    Collection<T> getAll();
}
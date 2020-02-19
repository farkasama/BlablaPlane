package com.example.jetty_jersey.DAO;

import java.util.List;

/*
 * Classe abstract pour les class DAO
 */

public abstract class DAO<T> {

    public abstract String put(T obj);

    public abstract boolean delete(T obj, String id);

    public abstract boolean update(T obj, String id);

    public abstract List<T> get(String id);
}

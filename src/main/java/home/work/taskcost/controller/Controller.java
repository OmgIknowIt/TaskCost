package home.work.taskcost.controller;

import java.sql.SQLException;
import java.util.Set;

public interface Controller<T> {

    public Long createNew(T o) throws SQLException;

    public Object getById(Long id);

    public int update(T o) throws SQLException;

    public Set<T> getAll();
}

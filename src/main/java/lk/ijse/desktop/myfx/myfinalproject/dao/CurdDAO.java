package lk.ijse.desktop.myfx.myfinalproject.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurdDAO<T, S> extends SuperDAO {
    List<T> getAll() throws SQLException;

    String getNextId() throws SQLException;

    boolean save(T t) throws SQLException;

    boolean update(T t) throws SQLException;

    boolean delete(String id) throws SQLException;

    List<String> getAllIds() throws SQLException;

    Optional<T> findById(String id) throws SQLException;
}

package lk.ijse.desktop.myfx.myfinalproject.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurdDAO<T, S> extends SuperDAO {
    List<T> getAll() throws SQLException;

    String getNextId() throws SQLException;

    boolean save(T t) throws SQLException;

    boolean update(T t) throws SQLException;

    boolean delete(S id) throws SQLException;

    List<S> getAllIds() throws SQLException;

    Optional<T> findById(S id) throws SQLException;
}

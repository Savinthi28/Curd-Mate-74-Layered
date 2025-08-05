package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Buffalo;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BuffaloDAO extends CurdDAO<Buffalo, String> {
    List<String> getAllBuffaloGenders() throws Exception;
    Optional<Buffalo> findBuffaloByHealthStatus(String healthStatus) throws SQLException;
}

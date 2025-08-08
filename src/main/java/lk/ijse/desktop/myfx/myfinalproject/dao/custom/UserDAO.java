package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.User;

import java.sql.SQLException;
import java.util.Optional;

public interface UserDAO extends CurdDAO<User, String> {
    Optional<User> getUserByEmail(String email) throws SQLException;
    boolean isValidUser(String userId, String password) throws SQLException;
}

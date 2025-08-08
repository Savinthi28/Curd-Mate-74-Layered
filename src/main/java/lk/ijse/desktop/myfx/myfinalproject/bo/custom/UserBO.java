package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.UserDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.ValidationException;

import java.sql.SQLException;
import java.util.List;

public interface UserBO extends SuperBO {
    List<UserDto> getAllUsers() throws SQLException;
    void saveUser(UserDto dto) throws DuplicateException, SQLException;
    void updateUser(UserDto dto) throws NotFoundException, SQLException;
    boolean deleteUser(String id) throws NotFoundException, SQLException;
    String getNextUserId() throws SQLException;
    UserDto findUserById(String id) throws SQLException;
    UserDto getUserByEmail(String email) throws SQLException;
    boolean validateUser(String userId, String password) throws SQLException, ValidationException;
}

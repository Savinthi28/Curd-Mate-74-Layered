package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.UserDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.UserBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.ValidationException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.UserDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserBOImpl implements UserBO {
    private final UserDAO userDAO = DAOFactory.getInstance().getDAO(DAOTypes.USER);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<UserDto> getAllUsers() throws SQLException {
        List<User> entities = userDAO.getAll();
        List<UserDto> dtos = new ArrayList<>();
        for (User entity : entities) {
            dtos.add(converter.getUserDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveUser(UserDto dto) throws DuplicateException, SQLException {
        Optional<User> existingUser = userDAO.findById(dto.getId());
        if (existingUser.isPresent()) {
            throw new DuplicateException("User with ID " + dto.getId() + " already exists.");
        }
        User entity = converter.getUser(dto);
        boolean saved = userDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save user record.");
        }
    }

    @Override
    public void updateUser(UserDto dto) throws NotFoundException, SQLException {
        Optional<User> existingUser = userDAO.findById(dto.getId());
        if (existingUser.isEmpty()) {
            throw new NotFoundException("User with ID " + dto.getId() + " not found.");
        }
        User entity = converter.getUser(dto);
        boolean updated = userDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update user record.");
        }
    }

    @Override
    public boolean deleteUser(String id) throws NotFoundException, SQLException {
        Optional<User> existingUser = userDAO.findById(id);
        if (existingUser.isEmpty()) {
            throw new NotFoundException("User with ID " + id + " not found.");
        }
        return userDAO.delete(id);
    }

    @Override
    public String getNextUserId() throws SQLException {
        return userDAO.getNextId();
    }

    @Override
    public UserDto findUserById(String id) throws SQLException {
        Optional<User> optionalUser = userDAO.findById(id);
        return optionalUser.map(converter::getUserDTO).orElse(null);
    }

    @Override
    public UserDto getUserByEmail(String email) throws SQLException {
        Optional<User> optionalUser = userDAO.getUserByEmail(email);
        return optionalUser.map(converter::getUserDTO).orElse(null);
    }

    @Override
    public boolean validateUser(String userId, String password) throws SQLException, ValidationException {
        if (userId == null || userId.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new ValidationException("User ID and password cannot be empty.");
        }
        return userDAO.isValidUser(userId, password);
    }
}

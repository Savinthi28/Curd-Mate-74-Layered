package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.UserDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    @Override
    public List<User> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM User");
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new User(
                    rs.getString("User_ID"),
                    rs.getString("User_Name"),
                    rs.getString("Password"),
                    rs.getString("Email")
            ));
        }
        return users;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT User_ID FROM User ORDER BY User_ID DESC LIMIT 1");
        char tableChar = 'U';
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.substring(1);
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            return String.format(tableChar + "%03d", nextIdNumber);
        }
        return tableChar + "001";
    }

    @Override
    public boolean save(User user) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO User (User_ID, User_Name, Password, Email) VALUES (?, ?, ?, ?)",
                user.getId(),
                user.getUserName(),
                user.getPassword(),
                user.getEmail()
        );
    }

    @Override
    public boolean update(User user) throws SQLException {
        return SQLUtil.execute(
                "UPDATE User SET User_Name = ?, Password = ?, Email = ? WHERE User_ID = ?",
                user.getUserName(),
                user.getPassword(),
                user.getEmail(),
                user.getId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM User WHERE User_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT User_ID FROM User");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<User> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM User WHERE User_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new User(
                    resultSet.getString("User_ID"),
                    resultSet.getString("User_Name"),
                    resultSet.getString("Password"),
                    resultSet.getString("Email")
            ));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email) throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM User WHERE Email = ?", email);
        if (rs.next()) {
            return Optional.of(new User(
                    rs.getString("User_ID"),
                    rs.getString("User_Name"),
                    rs.getString("Password"),
                    rs.getString("Email")
            ));
        }
        return Optional.empty();
    }

    @Override
    public boolean isValidUser(String userId, String password) throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM User WHERE User_ID = ? AND Password = ?", userId, password);
        return rs.next();
    }
}

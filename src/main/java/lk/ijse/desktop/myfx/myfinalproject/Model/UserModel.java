package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.UserDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserModel {

    public static boolean updateUser(UserDto userDto) throws SQLException {
        return CrudUtil.execute(
                "update User set User_Name = ?, Password = ?, Email = ? where User_ID = ?",
                userDto.getUserName(),
                userDto.getPassword(),
                userDto.getEmail(),
                userDto.getId()
        );
    }

    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select User_ID from User order by User_ID desc limit 1");
        char tableChar = 'U';
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNUmberString = lastId.substring(1);
            int lastIdNumber = Integer.parseInt(lastIdNUmberString);
            int nextIdNumber = lastIdNumber + 1;
            String nextIdString = String.format(tableChar + "%03d", nextIdNumber);
            return nextIdString;
        }
        return tableChar + "001";
    }

    public ArrayList<UserDto> viewAllUser() throws ClassNotFoundException, SQLException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM User");
        ArrayList<UserDto> user = new ArrayList<>();
        while (rs.next()) {
            UserDto userDto = new UserDto(
                    rs.getString("User_ID"),
                    rs.getString("User_Name"),
                    rs.getString("Password"),
                    rs.getString("Email")
            );
            user.add(userDto);
        }
        return user;
    }
    public boolean saveUser(UserDto userDto) throws ClassNotFoundException, SQLException {
        return CrudUtil.execute(
                "insert into User values (?,?,?,?)",
                userDto.getId(),
                userDto.getUserName(),
                userDto.getPassword(),
                userDto.getEmail()
        );
    }

    public boolean deleteUser(UserDto userDto) throws SQLException{
        String sql = "delete from User where User_ID=?";
        return CrudUtil.execute(sql, userDto.getId());
    }

    public UserDto getUserByEmail(String email) throws SQLException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM User WHERE Email = ?", email);
        if (rs.next()) {
            return new UserDto(
                    rs.getString("User_ID"),
                    rs.getString("User_Name"),
                    rs.getString("Password"),
                    rs.getString("Email")
            );
        }
        return null;
    }

    public boolean isValidUser(String userId, String password) throws SQLException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM User WHERE User_ID = ? AND Password = ?", userId, password);
        return rs.next();
    }
}
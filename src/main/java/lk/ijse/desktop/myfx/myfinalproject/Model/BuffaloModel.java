package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.BuffaloDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BuffaloModel {

    public static boolean updateBuffalo(BuffaloDto buffaloDto) throws SQLException {
        return CrudUtil.execute(
                "update Buffalo set Milk_Production = ?, Gender = ?, Age = ?, Health_Status = ? where Buffalo_ID = ?",
                buffaloDto.getMilkProduction(),
                buffaloDto.getGender(),
                buffaloDto.getAge(),
                buffaloDto.getHealthStatus(),
                buffaloDto.getBuffaloID()
        );
    }

    public static ArrayList<String> getAllBuffaloGender() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT DISTINCT Gender FROM Buffalo");
        ArrayList<String> list = new ArrayList<>();
        while (rst.next()) {
            String gender = rst.getString(1);
            list.add(gender);
        }
        return list;
    }

    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select buffalo_id from buffalo order by buffalo_id desc limit 1");
        String prefix = "BUF";
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.substring(prefix.length());
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            String nextIdString = String.format(prefix + "%03d", nextIdNumber);
            return nextIdString;
        }
        return prefix + "001";
    }

    public ArrayList<BuffaloDto> viewAllBuffalo() throws SQLException, ClassNotFoundException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Buffalo");
        ArrayList<BuffaloDto> viewBuffalo = new ArrayList<>();
        while (rs.next()) {
            BuffaloDto buffaloDto = new BuffaloDto(
                    rs.getString("Buffalo_ID"),
                    rs.getDouble("Milk_Production"),
                    rs.getString("Gender"),
                    rs.getInt("Age"),
                    rs.getString("Health_Status")
            );
            viewBuffalo.add(buffaloDto);
        }
        return viewBuffalo;
    }
    public boolean saveBuffalo(BuffaloDto buffaloDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "insert into Buffalo values (?,?,?,?,?)",
                buffaloDto.getBuffaloID(),
                buffaloDto.getMilkProduction(),
                buffaloDto.getGender(),
                buffaloDto.getAge(),
                buffaloDto.getHealthStatus()

        );
    }

    public boolean deleteBuffalo(BuffaloDto buffaloDto) throws SQLException {
        String sql = "delete from Buffalo where Buffalo_ID=?";
        return CrudUtil.execute(sql, buffaloDto.getBuffaloID());
    }
}


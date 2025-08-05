package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.BuffaloDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Buffalo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuffaloDAOImpl implements BuffaloDAO {
    @Override
    public List<String> getAllBuffaloGenders() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT DISTINCT Gender FROM Buffalo");
        List<String> list = new ArrayList<>();
        while (rst.next()) {
            String gender = rst.getString(1);
            list.add(gender);
        }
        return list;
    }

    @Override
    public Optional<Buffalo> findBuffaloByHealthStatus(String healthStatus) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Buffalo WHERE Health_Status = ?", healthStatus);
        if (resultSet.next()) {
            return Optional.of(new Buffalo(
                    resultSet.getString("Buffalo_ID"),
                    resultSet.getDouble("Milk_Production"),
                    resultSet.getString("Gender"),
                    resultSet.getInt("Age"),
                    resultSet.getString("Health_Status")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<Buffalo> getAll() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT * FROM Buffalo");
        List<Buffalo> buffaloList = new ArrayList<>();
        while (rst.next()) {
            buffaloList.add(new Buffalo(
                    rst.getString("Buffalo_ID"),
                    rst.getDouble("Milk_Production"),
                    rst.getString("Gender"),
                    rst.getInt("Age"),
                    rst.getString("Health_Status")
            ));
        }
        return buffaloList;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Buffalo_ID FROM Buffalo ORDER BY Buffalo_ID DESC LIMIT 1");
        String prefix = "BUF";
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.substring(prefix.length());
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            return String.format(prefix + "%03d", nextIdNumber);
        }
        return prefix + "001";
    }

    @Override
    public boolean save(Buffalo buffalo) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Buffalo (Buffalo_ID, Milk_Production, Gender, Age, Health_Status) VALUES (?, ?, ?, ?, ?)",
                buffalo.getBuffaloId(),
                buffalo.getMilkProduction(),
                buffalo.getGender(),
                buffalo.getAge(),
                buffalo.getHealthStatus()
        );
    }

    @Override
    public boolean update(Buffalo buffalo) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Buffalo SET Milk_Production = ?, Gender = ?, Age = ?, Health_Status = ? WHERE Buffalo_ID = ?",
                buffalo.getMilkProduction(),
                buffalo.getGender(),
                buffalo.getAge(),
                buffalo.getHealthStatus(),
                buffalo.getBuffaloId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Buffalo WHERE Buffalo_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Buffalo_ID FROM Buffalo");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<Buffalo> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Buffalo WHERE Buffalo_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new Buffalo(
                    resultSet.getString("Buffalo_ID"),
                    resultSet.getDouble("Milk_Production"),
                    resultSet.getString("Gender"),
                    resultSet.getInt("Age"),
                    resultSet.getString("Health_Status")
            ));
        }
        return Optional.empty();
    }
}

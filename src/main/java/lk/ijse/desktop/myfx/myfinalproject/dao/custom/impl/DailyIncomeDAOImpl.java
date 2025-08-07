package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.DailyIncomeDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.DailyIncome;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DailyIncomeDAOImpl implements DailyIncomeDAO {
    @Override
    public List<DailyIncome> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Daily_Income");
        List<DailyIncome> dailyIncomeList = new ArrayList<>();
        while (rs.next()) {
            dailyIncomeList.add(new DailyIncome(
                    rs.getString("Income_ID"),
                    rs.getString("Customer_Name"),
                    rs.getString("Income_Date"),
                    rs.getString("Description"),
                    rs.getDouble("Amount")
            ));
        }
        return dailyIncomeList;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Income_ID FROM Daily_Income ORDER BY Income_ID DESC LIMIT 1");
        String prefix = "DI";
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
    public boolean save(DailyIncome dailyIncome) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Daily_Income (Income_ID, Customer_Name, Income_Date, Description, Amount) VALUES (?, ?, ?, ?, ?)",
                dailyIncome.getId(),
                dailyIncome.getCustomerName(),
                dailyIncome.getDate(),
                dailyIncome.getDescription(),
                dailyIncome.getAmount()
        );
    }

    @Override
    public boolean update(DailyIncome dailyIncome) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Daily_Income SET Customer_Name = ?, Income_Date = ?, Description = ?, Amount = ? WHERE Income_ID = ?",
                dailyIncome.getCustomerName(),
                dailyIncome.getDate(),
                dailyIncome.getDescription(),
                dailyIncome.getAmount(),
                dailyIncome.getId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Daily_Income WHERE Income_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Income_ID FROM Daily_Income");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<DailyIncome> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Daily_Income WHERE Income_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new DailyIncome(
                    resultSet.getString("Income_ID"),
                    resultSet.getString("Customer_Name"),
                    resultSet.getString("Income_Date"),
                    resultSet.getString("Description"),
                    resultSet.getDouble("Amount")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllIncomeDescriptions() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT DISTINCT Description FROM Daily_Income");
        List<String> list = new ArrayList<>();
        while (rst.next()) {
            list.add(rst.getString(1));
        }
        return list;
    }
}
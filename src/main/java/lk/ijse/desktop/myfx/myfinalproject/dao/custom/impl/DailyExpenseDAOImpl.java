package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.DailyExpenseDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.DailyExpense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DailyExpenseDAOImpl implements DailyExpenseDAO {
    @Override
    public List<DailyExpense> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Expense");
        List<DailyExpense> dailyExpenseList = new ArrayList<>();
        while (rs.next()) {
            dailyExpenseList.add(new DailyExpense(
                    rs.getString("Expense_ID"),
                    rs.getString("Expense_Date"),
                    rs.getString("Description"),
                    rs.getDouble("Amount"),
                    rs.getBoolean("Daily_Expense")
            ));
        }
        return dailyExpenseList;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Expense_ID FROM Expense ORDER BY Expense_ID DESC LIMIT 1");
        char tableChar = 'E';
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
    public boolean save(DailyExpense dailyExpense) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Expense (Expense_ID, Expense_Date, Description, Amount, Daily_Expense) VALUES (?, ?, ?, ?, ?)",
                dailyExpense.getId(),
                dailyExpense.getDate(),
                dailyExpense.getDescription(),
                dailyExpense.getAmount(),
                dailyExpense.isDailyExpense()
        );
    }

    @Override
    public boolean update(DailyExpense dailyExpense) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Expense SET Expense_Date = ?, Description = ?, Amount = ?, Daily_Expense = ? WHERE Expense_Id = ?",
                dailyExpense.getDate(),
                dailyExpense.getDescription(),
                dailyExpense.getAmount(),
                dailyExpense.isDailyExpense(),
                dailyExpense.getId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Expense WHERE Expense_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Expense_ID FROM Expense");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<DailyExpense> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Expense WHERE Expense_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new DailyExpense(
                    resultSet.getString("Expense_ID"),
                    resultSet.getString("Expense_Date"),
                    resultSet.getString("Description"),
                    resultSet.getDouble("Amount"),
                    resultSet.getBoolean("Daily_Expense")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<Boolean> getAllExpenseCategories() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT DISTINCT Daily_Expense FROM Expense");
        List<Boolean> list = new ArrayList<>();
        while (rst.next()) {
            list.add(rst.getBoolean(1));
        }
        return list;
    }
}
package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.DailyExpenseDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DailyExpenseModel {

    public static boolean updateDailyExpense(DailyExpenseDto dailyExpenseDto) throws SQLException {
        return CrudUtil.execute(
                "update Expense set Expense_Date = ?, Description = ?, Amount = ?, Daily_Expense = ? where Expense_Id = ?",
                dailyExpenseDto.getDate(),
                dailyExpenseDto.getDescription(),
                dailyExpenseDto.getAmount(),
                dailyExpenseDto.isDailyExpense(),
                dailyExpenseDto.getId()
        );
    }

    public static ArrayList<Boolean> getAllExpense() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT DISTINCT Daily_Expense FROM Expense");
        ArrayList<Boolean> list = new ArrayList<>();
        while (rst.next()) {
            Boolean expense = rst.getBoolean(1);
            list.add(expense);
        }
        return list;
    }

    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select Expense_ID from Expense order by Expense_ID desc limit 1");
        char tableChar = 'E';
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

    public ArrayList<DailyExpenseDto> viewAllDailyExpense() throws SQLException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Expense");
        ArrayList<DailyExpenseDto> dailyExpenseDto = new ArrayList<>();
        while (rs.next()) {
            DailyExpenseDto temp = new DailyExpenseDto(
                    rs.getString("Expense_ID"),
                    rs.getString("Expense_Date"),
                    rs.getString("Description"),
                    rs.getDouble("Amount"),
                    rs.getBoolean("Daily_Expense")
            );
            dailyExpenseDto.add(temp);
        }
        return dailyExpenseDto;
    }
    public boolean SavedDailyExpense(DailyExpenseDto expenseDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "insert into Expense values (?,?,?,?,?)",
                expenseDto.getId(),
                expenseDto.getDate(),
                expenseDto.getDescription(),
                expenseDto.getAmount(),
                expenseDto.isDailyExpense()
        );
    }

    public boolean deleteDailyExpense(DailyExpenseDto dailyExpenseDto) throws SQLException{
        String sql = "delete from Expense where Expense_ID=?";
        return CrudUtil.execute(sql, dailyExpenseDto.getId());
    }
}

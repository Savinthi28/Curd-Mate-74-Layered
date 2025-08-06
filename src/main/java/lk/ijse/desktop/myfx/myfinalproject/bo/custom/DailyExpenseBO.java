package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.DailyExpenseDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface DailyExpenseBO extends SuperBO {
    List<DailyExpenseDto> getAllDailyExpenses() throws SQLException;
    void saveDailyExpense(DailyExpenseDto dto) throws DuplicateException, SQLException;
    void updateDailyExpense(DailyExpenseDto dto) throws NotFoundException, SQLException;
    boolean deleteDailyExpense(String id) throws NotFoundException, SQLException;
    String getNextDailyExpenseId() throws SQLException;
    DailyExpenseDto findDailyExpenseById(String id) throws SQLException;
    List<Boolean> getAllExpenseCategories() throws SQLException;
}

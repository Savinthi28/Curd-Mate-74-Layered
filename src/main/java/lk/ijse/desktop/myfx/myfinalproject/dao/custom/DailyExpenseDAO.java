package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.DailyExpense;

import java.sql.SQLException;
import java.util.List;

public interface DailyExpenseDAO extends CurdDAO<DailyExpense, String> {
    List<Boolean> getAllExpenseCategories() throws SQLException;
}

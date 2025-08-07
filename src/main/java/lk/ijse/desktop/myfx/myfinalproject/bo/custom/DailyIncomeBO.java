package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.DailyIncomeDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface DailyIncomeBO extends SuperBO {
    List<DailyIncomeDto> getAllDailyIncomes() throws SQLException;
    void saveDailyIncome(DailyIncomeDto dto) throws DuplicateException, SQLException;
    void updateDailyIncome(DailyIncomeDto dto) throws NotFoundException, SQLException;
    boolean deleteDailyIncome(String id) throws NotFoundException, SQLException;
    String getNextDailyIncomeId() throws SQLException;
    DailyIncomeDto findDailyIncomeById(String id) throws SQLException;
    List<String> getAllIncomeDescriptions() throws SQLException;
}

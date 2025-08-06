package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.DailyExpenseDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.DailyExpenseBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.DailyExpenseDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.DailyExpense;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DailyExpenseBOImpl implements DailyExpenseBO {
    private final DailyExpenseDAO dailyExpenseDAO = DAOFactory.getInstance().getDAO(DAOTypes.DAILY_EXPENSE);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<DailyExpenseDto> getAllDailyExpenses() throws SQLException {
        List<DailyExpense> entities = dailyExpenseDAO.getAll();
        List<DailyExpenseDto> dtos = new ArrayList<>();
        for (DailyExpense entity : entities) {
            dtos.add(converter.getDailyExpenseDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveDailyExpense(DailyExpenseDto dto) throws DuplicateException, SQLException {
        Optional<DailyExpense> existingExpense = dailyExpenseDAO.findById(dto.getId());
        if (existingExpense.isPresent()) {
            throw new DuplicateException("Daily Expense ID " + dto.getId() + " already exists.");
        }
        DailyExpense entity = converter.getDailyExpense(dto);
        boolean saved = dailyExpenseDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save daily expense record.");
        }
    }

    @Override
    public void updateDailyExpense(DailyExpenseDto dto) throws NotFoundException, SQLException {
        Optional<DailyExpense> existingExpense = dailyExpenseDAO.findById(dto.getId());
        if (existingExpense.isEmpty()) {
            throw new NotFoundException("Daily Expense ID " + dto.getId() + " not found.");
        }
        DailyExpense entity = converter.getDailyExpense(dto);
        boolean updated = dailyExpenseDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update daily expense record.");
        }
    }

    @Override
    public boolean deleteDailyExpense(String id) throws NotFoundException, SQLException {
        Optional<DailyExpense> existingExpense = dailyExpenseDAO.findById(id);
        if (existingExpense.isEmpty()) {
            throw new NotFoundException("Daily Expense ID " + id + " not found.");
        }
        return dailyExpenseDAO.delete(id);
    }

    @Override
    public String getNextDailyExpenseId() throws SQLException {
        return dailyExpenseDAO.getNextId();
    }

    @Override
    public DailyExpenseDto findDailyExpenseById(String id) throws SQLException {
        Optional<DailyExpense> optionalExpense = dailyExpenseDAO.findById(id);
        return optionalExpense.map(converter::getDailyExpenseDTO).orElse(null);
    }

    @Override
    public List<Boolean> getAllExpenseCategories() throws SQLException {
        return dailyExpenseDAO.getAllExpenseCategories();
    }
}
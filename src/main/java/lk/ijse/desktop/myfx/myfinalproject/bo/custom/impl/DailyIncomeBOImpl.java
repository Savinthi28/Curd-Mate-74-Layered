package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.DailyIncomeDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.DailyIncomeBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.DailyIncomeDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.DailyIncome;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DailyIncomeBOImpl implements DailyIncomeBO {
    private final DailyIncomeDAO dailyIncomeDAO = DAOFactory.getInstance().getDAO(DAOTypes.DAILY_INCOME);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<DailyIncomeDto> getAllDailyIncomes() throws SQLException {
        List<DailyIncome> entities = dailyIncomeDAO.getAll();
        List<DailyIncomeDto> dtos = new ArrayList<>();
        for (DailyIncome entity : entities) {
            dtos.add(converter.getDailyIncomeDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveDailyIncome(DailyIncomeDto dto) throws DuplicateException, SQLException {
        Optional<DailyIncome> existingIncome = dailyIncomeDAO.findById(dto.getId());
        if (existingIncome.isPresent()) {
            throw new DuplicateException("Daily Income ID " + dto.getId() + " already exists.");
        }
        DailyIncome entity = converter.getDailyIncome(dto);
        boolean saved = dailyIncomeDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save daily income record.");
        }
    }

    @Override
    public void updateDailyIncome(DailyIncomeDto dto) throws NotFoundException, SQLException {
        Optional<DailyIncome> existingIncome = dailyIncomeDAO.findById(dto.getId());
        if (existingIncome.isEmpty()) {
            throw new NotFoundException("Daily Income ID " + dto.getId() + " not found.");
        }
        DailyIncome entity = converter.getDailyIncome(dto);
        boolean updated = dailyIncomeDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update daily income record.");
        }
    }

    @Override
    public boolean deleteDailyIncome(String id) throws NotFoundException, SQLException {
        Optional<DailyIncome> existingIncome = dailyIncomeDAO.findById(id);
        if (existingIncome.isEmpty()) {
            throw new NotFoundException("Daily Income ID " + id + " not found.");
        }
        return dailyIncomeDAO.delete(id);
    }

    @Override
    public String getNextDailyIncomeId() throws SQLException {
        return dailyIncomeDAO.getNextId();
    }

    @Override
    public DailyIncomeDto findDailyIncomeById(String id) throws SQLException {
        Optional<DailyIncome> optionalIncome = dailyIncomeDAO.findById(id);
        return optionalIncome.map(converter::getDailyIncomeDTO).orElse(null);
    }

    @Override
    public List<String> getAllIncomeDescriptions() throws SQLException {
        return dailyIncomeDAO.getAllIncomeDescriptions();
    }
}
package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.MilkStorageDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.MilkStorageBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.MilkStorageDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.MilkStorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MilkStorageBOImpl implements MilkStorageBO {
    private final MilkStorageDAO milkStorageDAO = DAOFactory.getInstance().getDAO(DAOTypes.MILK_STORAGE);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<MilkStorageDto> getAllMilkStorages() throws SQLException {
        List<MilkStorage> entities = milkStorageDAO.getAll();
        List<MilkStorageDto> dtos = new ArrayList<>();
        for (MilkStorage entity : entities) {
            dtos.add(converter.getMilkStorageDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveMilkStorage(MilkStorageDto dto) throws DuplicateException, SQLException {
        Optional<MilkStorage> existingStorage = milkStorageDAO.findById(dto.getStorageId());
        if (existingStorage.isPresent()) {
            throw new DuplicateException("Milk Storage ID " + dto.getStorageId() + " already exists.");
        }
        MilkStorage entity = converter.getMilkStorage(dto);
        boolean saved = milkStorageDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save milk storage record.");
        }
    }

    @Override
    public void updateMilkStorage(MilkStorageDto dto) throws NotFoundException, SQLException {
        Optional<MilkStorage> existingStorage = milkStorageDAO.findById(dto.getStorageId());
        if (existingStorage.isEmpty()) {
            throw new NotFoundException("Milk Storage ID " + dto.getStorageId() + " not found.");
        }
        MilkStorage entity = converter.getMilkStorage(dto);
        boolean updated = milkStorageDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update milk storage record.");
        }
    }

    @Override
    public boolean deleteMilkStorage(String id) throws NotFoundException, SQLException {
        Optional<MilkStorage> existingStorage = milkStorageDAO.findById(id);
        if (existingStorage.isEmpty()) {
            throw new NotFoundException("Milk Storage ID " + id + " not found.");
        }
        return milkStorageDAO.delete(id);
    }

    @Override
    public String getNextMilkStorageId() throws SQLException {
        return milkStorageDAO.getNextId();
    }

    @Override
    public MilkStorageDto findMilkStorageById(String id) throws SQLException {
        Optional<MilkStorage> optionalStorage = milkStorageDAO.findById(id);
        return optionalStorage.map(converter::getMilkStorageDTO).orElse(null);
    }

    @Override
    public List<String> getAllCollectionIds() throws SQLException {
        return milkStorageDAO.getAllCollectionIds();
    }
}
package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.CurdProductionDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.CurdProductionBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.InUseException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.CurdProductionDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.CurdProduction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurdProductionBOImpl implements CurdProductionBO {

    private final CurdProductionDAO curdProductionDAO = DAOFactory.getInstance().getDAO(DAOTypes.CURD_PRODUCTION);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<CurdProductionDto> getAllCurdProductions() throws SQLException {
        List<CurdProduction> entities = curdProductionDAO.getAll();
        List<CurdProductionDto> dtos = new ArrayList<>();
        for (CurdProduction entity : entities) {
            dtos.add(converter.getCurdProductionDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveCurdProduction(CurdProductionDto dto) throws DuplicateException, SQLException {
        Optional<CurdProduction> existingCurd = curdProductionDAO.findById(dto.getProductionId());
        if (existingCurd.isPresent()) {
            throw new DuplicateException("Curd Production ID " + dto.getProductionId() + " already exists.");
        }
        CurdProduction entity = converter.getCurdProduction(dto);
        boolean saved = curdProductionDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save curd production record.");
        }
    }

    @Override
    public void updateCurdProduction(CurdProductionDto dto) throws NotFoundException, SQLException {
        Optional<CurdProduction> existingCurd = curdProductionDAO.findById(dto.getProductionId());
        if (existingCurd.isEmpty()) {
            throw new NotFoundException("Curd Production ID " + dto.getProductionId() + " not found.");
        }
        CurdProduction entity = converter.getCurdProduction(dto);
        boolean updated = curdProductionDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update curd production record.");
        }
    }

    @Override
    public boolean deleteCurdProduction(String productionId) throws NotFoundException, InUseException, SQLException {
        Optional<CurdProduction> existingCurd = curdProductionDAO.findById(productionId);
        if (existingCurd.isEmpty()) {
            throw new NotFoundException("Curd Production ID " + productionId + " not found.");
        }
        try {
            return curdProductionDAO.delete(productionId);
        } catch (SQLException e) {
            if (e.getMessage().contains("Cannot delete or update a parent row: a foreign key constraint fails")) {
                throw new InUseException("Curd Production ID " + productionId + " is linked to other records (e.g., Order Details) and cannot be deleted.");
            }
            throw new SQLException("Error deleting curd production record: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextCurdProductionId() throws SQLException {
        return curdProductionDAO.getNextId();
    }

    @Override
    public CurdProductionDto findCurdProductionById(String productionId) throws SQLException {
        Optional<CurdProduction> optionalCurd = curdProductionDAO.findById(productionId);
        return optionalCurd.map(converter::getCurdProductionDTO).orElse(null);
    }

    @Override
    public List<Integer> getAllPotsSizes() throws SQLException {
        return curdProductionDAO.getAllPotsSizes();
    }

    @Override
    public List<String> getAllStorageIds() throws SQLException {
        return curdProductionDAO.getAllStorageIds();
    }

    @Override
    public Optional<Integer> getPotsSizeByProductionId(String productionId) throws SQLException {
        return curdProductionDAO.findPotsSizeById(productionId);
    }

    @Override
    public List<String> getAllCurdProductionIds() throws SQLException {
        return curdProductionDAO.getAllProductionIds();
    }

    @Override
    public boolean reduceCurdQuantity(String productionId, int quantity) throws SQLException {
        // Business logic for reducing quantity, e.g., check if sufficient quantity exists
        Optional<CurdProduction> curdProduction = curdProductionDAO.findById(productionId);
        if (curdProduction.isEmpty()) {
            throw new NotFoundException("Curd Production with ID " + productionId + " not found for quantity reduction.");
        }
        if (curdProduction.get().getQuantity() < quantity) {
            throw new SQLException("Insufficient quantity of curd production (ID: " + productionId + "). Available: " + curdProduction.get().getQuantity() + ", Requested: " + quantity);
        }
        return curdProductionDAO.reduceQuantity(productionId, quantity);
    }
}
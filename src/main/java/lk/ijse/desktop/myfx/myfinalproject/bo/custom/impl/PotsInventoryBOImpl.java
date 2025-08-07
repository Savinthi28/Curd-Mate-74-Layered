package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.PotsInventoryDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.PotsInventoryBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.PotsInventoryDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.PotsInventory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotsInventoryBOImpl implements PotsInventoryBO {
    private final PotsInventoryDAO potsInventoryDAO = DAOFactory.getInstance().getDAO(DAOTypes.POTS_INVENTORY);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<PotsInventoryDto> getAllPotsInventory() throws SQLException {
        List<PotsInventory> entities = potsInventoryDAO.getAll();
        List<PotsInventoryDto> dtos = new ArrayList<>();
        for (PotsInventory entity : entities) {
            dtos.add(converter.getPotsInventoryDTO(entity));
        }
        return dtos;
    }

    @Override
    public void savePotsInventory(PotsInventoryDto dto) throws DuplicateException, SQLException {
        Optional<PotsInventory> existingPots = potsInventoryDAO.findById(dto.getId());
        if (existingPots.isPresent()) {
            throw new DuplicateException("Pots Inventory ID " + dto.getId() + " already exists.");
        }
        PotsInventory entity = converter.getPotsInventory(dto);
        boolean saved = potsInventoryDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save pots inventory record.");
        }
    }

    @Override
    public void updatePotsInventory(PotsInventoryDto dto) throws NotFoundException, SQLException {
        Optional<PotsInventory> existingPots = potsInventoryDAO.findById(dto.getId());
        if (existingPots.isEmpty()) {
            throw new NotFoundException("Pots Inventory ID " + dto.getId() + " not found.");
        }
        PotsInventory entity = converter.getPotsInventory(dto);
        boolean updated = potsInventoryDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update pots inventory record.");
        }
    }

    @Override
    public boolean deletePotsInventory(String id) throws NotFoundException, SQLException {
        Optional<PotsInventory> existingPots = potsInventoryDAO.findById(id);
        if (existingPots.isEmpty()) {
            throw new NotFoundException("Pots Inventory ID " + id + " not found.");
        }
        return potsInventoryDAO.delete(id);
    }

    @Override
    public String getNextPotsInventoryId() throws SQLException {
        return potsInventoryDAO.getNextId();
    }

    @Override
    public PotsInventoryDto findPotsInventoryById(String id) throws SQLException {
        Optional<PotsInventory> optionalPots = potsInventoryDAO.findById(id);
        return optionalPots.map(converter::getPotsInventoryDTO).orElse(null);
    }

    @Override
    public List<Integer> getAllPotsSizes() throws SQLException {
        return potsInventoryDAO.getAllPotsSize();
    }
}

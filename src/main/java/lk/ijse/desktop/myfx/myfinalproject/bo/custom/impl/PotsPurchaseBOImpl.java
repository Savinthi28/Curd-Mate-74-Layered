package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.PotsPurchaseDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.PotsPurchaseBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.PotsPurchaseDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.PotsPurchase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotsPurchaseBOImpl implements PotsPurchaseBO {
    private final PotsPurchaseDAO potsPurchaseDAO = DAOFactory.getInstance().getDAO(DAOTypes.POTS_PURCHASE);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<PotsPurchaseDto> getAllPotsPurchases() throws SQLException {
        List<PotsPurchase> entities = potsPurchaseDAO.getAll();
        List<PotsPurchaseDto> dtos = new ArrayList<>();
        for (PotsPurchase entity : entities) {
            dtos.add(converter.getPotsPurchaseDTO(entity));
        }
        return dtos;
    }

    @Override
    public void savePotsPurchase(PotsPurchaseDto dto) throws DuplicateException, SQLException {
        Optional<PotsPurchase> existingPurchase = potsPurchaseDAO.findById(dto.getPurchaseId());
        if (existingPurchase.isPresent()) {
            throw new DuplicateException("Pots Purchase ID " + dto.getPurchaseId() + " already exists.");
        }
        PotsPurchase entity = converter.getPotsPurchase(dto);
        boolean saved = potsPurchaseDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save pots purchase record.");
        }
    }

    @Override
    public void updatePotsPurchase(PotsPurchaseDto dto) throws NotFoundException, SQLException {
        Optional<PotsPurchase> existingPurchase = potsPurchaseDAO.findById(dto.getPurchaseId());
        if (existingPurchase.isEmpty()) {
            throw new NotFoundException("Pots Purchase ID " + dto.getPurchaseId() + " not found.");
        }
        PotsPurchase entity = converter.getPotsPurchase(dto);
        boolean updated = potsPurchaseDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update pots purchase record.");
        }
    }

    @Override
    public boolean deletePotsPurchase(String id) throws NotFoundException, SQLException {
        Optional<PotsPurchase> existingPurchase = potsPurchaseDAO.findById(id);
        if (existingPurchase.isEmpty()) {
            throw new NotFoundException("Pots Purchase ID " + id + " not found.");
        }
        return potsPurchaseDAO.delete(id);
    }

    @Override
    public String getNextPotsPurchaseId() throws SQLException {
        return potsPurchaseDAO.getNextId();
    }

    @Override
    public PotsPurchaseDto findPotsPurchaseById(String id) throws SQLException {
        Optional<PotsPurchase> optionalPurchase = potsPurchaseDAO.findById(id);
        return optionalPurchase.map(converter::getPotsPurchaseDTO).orElse(null);
    }

    @Override
    public List<Integer> getAllPotsSizesForPurchase() throws SQLException {
        return potsPurchaseDAO.getAllPotsSizeFromInventory();
    }
}

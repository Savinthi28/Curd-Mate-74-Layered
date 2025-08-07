package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.RawMaterialPurchaseDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.RawMaterialPurchaseBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.RawMaterialPurchaseDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.RawMaterialPurchase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RawMaterialPurchaseBOImpl implements RawMaterialPurchaseBO {
    private final RawMaterialPurchaseDAO rawMaterialPurchaseDAO = DAOFactory.getInstance().getDAO(DAOTypes.RAW_MATERIAL_PURCHASE);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<RawMaterialPurchaseDto> getAllRawMaterialPurchases() throws SQLException {
        List<RawMaterialPurchase> entities = rawMaterialPurchaseDAO.getAll();
        List<RawMaterialPurchaseDto> dtos = new ArrayList<>();
        for (RawMaterialPurchase entity : entities) {
            dtos.add(converter.getRawMaterialPurchaseDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveRawMaterialPurchase(RawMaterialPurchaseDto dto) throws DuplicateException, SQLException {
        Optional<RawMaterialPurchase> existingPurchase = rawMaterialPurchaseDAO.findById(dto.getPurchaseId());
        if (existingPurchase.isPresent()) {
            throw new DuplicateException("Raw Material Purchase ID " + dto.getPurchaseId() + " already exists.");
        }
        RawMaterialPurchase entity = converter.getRawMaterialPurchase(dto);
        boolean saved = rawMaterialPurchaseDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save raw material purchase record.");
        }
    }

    @Override
    public void updateRawMaterialPurchase(RawMaterialPurchaseDto dto) throws NotFoundException, SQLException {
        Optional<RawMaterialPurchase> existingPurchase = rawMaterialPurchaseDAO.findById(dto.getPurchaseId());
        if (existingPurchase.isEmpty()) {
            throw new NotFoundException("Raw Material Purchase ID " + dto.getPurchaseId() + " not found.");
        }
        RawMaterialPurchase entity = converter.getRawMaterialPurchase(dto);
        boolean updated = rawMaterialPurchaseDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update raw material purchase record.");
        }
    }

    @Override
    public boolean deleteRawMaterialPurchase(String id) throws NotFoundException, SQLException {
        Optional<RawMaterialPurchase> existingPurchase = rawMaterialPurchaseDAO.findById(id);
        if (existingPurchase.isEmpty()) {
            throw new NotFoundException("Raw Material Purchase ID " + id + " not found.");
        }
        return rawMaterialPurchaseDAO.delete(id);
    }

    @Override
    public String getNextRawMaterialPurchaseId() throws SQLException {
        return rawMaterialPurchaseDAO.getNextId();
    }

    @Override
    public RawMaterialPurchaseDto findRawMaterialPurchaseById(String id) throws SQLException {
        Optional<RawMaterialPurchase> optionalPurchase = rawMaterialPurchaseDAO.findById(id);
        return optionalPurchase.map(converter::getRawMaterialPurchaseDTO).orElse(null);
    }

    @Override
    public List<String> getAllSupplierIds() throws SQLException {
        return rawMaterialPurchaseDAO.getAllSupplierIds();
    }
}

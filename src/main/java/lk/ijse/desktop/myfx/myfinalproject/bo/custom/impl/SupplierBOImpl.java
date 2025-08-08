package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.SupplierDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.SupplierBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.SupplierDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Supplier;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierBOImpl implements SupplierBO {
    private final SupplierDAO supplierDAO = DAOFactory.getInstance().getDAO(DAOTypes.SUPPLIER);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<SupplierDto> getAllSuppliers() throws SQLException {
        List<Supplier> entities = supplierDAO.getAll();
        List<SupplierDto> dtos = new ArrayList<>();
        for (Supplier entity : entities) {
            dtos.add(converter.getSupplierDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveSupplier(SupplierDto dto) throws DuplicateException, SQLException {
        Optional<Supplier> existingSupplier = supplierDAO.findById(dto.getSupplierId());
        if (existingSupplier.isPresent()) {
            throw new DuplicateException("Supplier with ID " + dto.getSupplierId() + " already exists.");
        }
        Supplier entity = converter.getSupplier(dto);
        boolean saved = supplierDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save supplier record.");
        }
    }

    @Override
    public void updateSupplier(SupplierDto dto) throws NotFoundException, SQLException {
        Optional<Supplier> existingSupplier = supplierDAO.findById(dto.getSupplierId());
        if (existingSupplier.isEmpty()) {
            throw new NotFoundException("Supplier with ID " + dto.getSupplierId() + " not found.");
        }
        Supplier entity = converter.getSupplier(dto);
        boolean updated = supplierDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update supplier record.");
        }
    }

    @Override
    public boolean deleteSupplier(String id) throws NotFoundException, SQLException {
        Optional<Supplier> existingSupplier = supplierDAO.findById(id);
        if (existingSupplier.isEmpty()) {
            throw new NotFoundException("Supplier with ID " + id + " not found.");
        }
        return supplierDAO.delete(id);
    }

    @Override
    public String getNextSupplierId() throws SQLException {
        return supplierDAO.getNextId();
    }

    @Override
    public SupplierDto findSupplierById(String id) throws SQLException {
        Optional<Supplier> optionalSupplier = supplierDAO.findById(id);
        return optionalSupplier.map(converter::getSupplierDTO).orElse(null);
    }
}

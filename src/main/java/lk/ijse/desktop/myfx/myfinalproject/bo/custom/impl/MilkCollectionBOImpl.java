package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.MilkCollectionDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.MilkCollectionBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.MilkCollectionDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.MilkCollection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MilkCollectionBOImpl implements MilkCollectionBO {
    private final MilkCollectionDAO milkCollectionDAO = DAOFactory.getInstance().getDAO(DAOTypes.MILK_COLLECTION);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<MilkCollectionDto> getAllMilkCollections() throws SQLException {
        List<MilkCollection> entities = milkCollectionDAO.getAll();
        List<MilkCollectionDto> dtos = new ArrayList<>();
        for (MilkCollection entity : entities) {
            dtos.add(converter.getMilkCollectionDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveMilkCollection(MilkCollectionDto dto) throws DuplicateException, SQLException {
        Optional<MilkCollection> existingCollection = milkCollectionDAO.findById(dto.getId());
        if (existingCollection.isPresent()) {
            throw new DuplicateException("Milk Collection ID " + dto.getId() + " already exists.");
        }
        MilkCollection entity = converter.getMilkCollection(dto);
        boolean saved = milkCollectionDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save milk collection record.");
        }
    }

    @Override
    public void updateMilkCollection(MilkCollectionDto dto) throws NotFoundException, SQLException {
        Optional<MilkCollection> existingCollection = milkCollectionDAO.findById(dto.getId());
        if (existingCollection.isEmpty()) {
            throw new NotFoundException("Milk Collection ID " + dto.getId() + " not found.");
        }
        MilkCollection entity = converter.getMilkCollection(dto);
        boolean updated = milkCollectionDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update milk collection record.");
        }
    }

    @Override
    public boolean deleteMilkCollection(String id) throws NotFoundException, SQLException {
        Optional<MilkCollection> existingCollection = milkCollectionDAO.findById(id);
        if (existingCollection.isEmpty()) {
            throw new NotFoundException("Milk Collection ID " + id + " not found.");
        }
        return milkCollectionDAO.delete(id);
    }

    @Override
    public String getNextMilkCollectionId() throws SQLException {
        return milkCollectionDAO.getNextId();
    }

    @Override
    public MilkCollectionDto findMilkCollectionById(String id) throws SQLException {
        Optional<MilkCollection> optionalCollection = milkCollectionDAO.findById(id);
        return optionalCollection.map(converter::getMilkCollectionDTO).orElse(null);
    }

    @Override
    public List<String> getAllBuffaloIds() throws SQLException {
        return milkCollectionDAO.getAllBuffaloIds();
    }
}
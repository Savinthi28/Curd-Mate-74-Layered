package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.BuffaloDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.BuffaloBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.InUseException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.BuffaloDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Buffalo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuffaloBOImpl implements BuffaloBO {

    private final BuffaloDAO buffaloDAO = DAOFactory.getInstance().getDAO(DAOTypes.BUFFALO);
    private final EntityDTOConverter converter = new EntityDTOConverter();
    @Override
    public List<BuffaloDto> getAllBuffaloes() throws SQLException {
        List<Buffalo> buffaloes = buffaloDAO.getAll();
        List<BuffaloDto> buffaloDtos = new ArrayList<>();
        for (Buffalo buffalo : buffaloes) {
            buffaloDtos.add(converter.getBuffaloDTO(buffalo));
        }
        return buffaloDtos;
    }

    @Override
    public void saveBuffalo(BuffaloDto dto) throws DuplicateException, SQLException {
        Optional<Buffalo> optionalBuffalo = buffaloDAO.findById(dto.getBuffaloID());
        if (optionalBuffalo.isPresent()) {
            throw new DuplicateException("Buffalo_ID" + dto.getBuffaloID() + " already exist");
        }
        Buffalo buffalo = converter.getBuffalo(dto);
        boolean saved = buffaloDAO.save(buffalo);
        if (!saved) {
            throw new SQLException("Failed to save buffalo");
        }
    }

    @Override
    public void updateBuffalo(BuffaloDto dto) throws NotFoundException, SQLException {
        Optional<Buffalo> optionalBuffalo = buffaloDAO.findById(dto.getBuffaloID());
        if (optionalBuffalo.isEmpty()) {
            throw new NotFoundException("Buffalo_ID" + dto.getBuffaloID() + " not found");
        }
        Buffalo buffalo = converter.getBuffalo(dto);
        boolean updated = buffaloDAO.update(buffalo);
        if (!updated) {
            throw new SQLException("Failed to update buffalo");
        }
    }

    @Override
    public boolean deleteBuffalo(String id) throws NotFoundException, InUseException, SQLException {
        Optional<Buffalo> optionalBuffalo = buffaloDAO.findById(id);
        if (optionalBuffalo.isEmpty()) {
            throw new NotFoundException("Buffalo_ID" + id + " not found");
        }
        try {
            return buffaloDAO.delete(id);
        }catch (SQLException e) {
            if (e.getMessage().contains("Cannot delete or update a parent row: a foreign key constraint fails")) {
                throw new InUseException("Buffalo ID " + id + " is linked to other records and cannot be deleted.");
            }
            throw new SQLException("Error deleting buffalo record: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextBuffaloId() throws SQLException {
        return buffaloDAO.getNextId();
    }

    @Override
    public BuffaloDto findBuffaloById(String id) throws SQLException {
        Optional<Buffalo> optionalBuffalo = buffaloDAO.findById(id);
        return optionalBuffalo.map(converter::getBuffaloDTO).orElse(null);
    }

    @Override
    public List<String> getAllBuffaloGender() throws Exception {
        return buffaloDAO.getAllBuffaloGenders();
    }

}

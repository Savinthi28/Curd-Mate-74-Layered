package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.QualityCheckDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.QualityCheckBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.QualityCheckDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.QualityCheck;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QualityCheckBOImpl implements QualityCheckBO {
    private final QualityCheckDAO qualityCheckDAO = DAOFactory.getInstance().getDAO(DAOTypes.QUALITY_CHECK);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<QualityCheckDto> getAllQualityChecks() throws SQLException {
        List<QualityCheck> entities = qualityCheckDAO.getAll();
        List<QualityCheckDto> dtos = new ArrayList<>();
        for (QualityCheck entity : entities) {
            dtos.add(converter.getQualityCheckDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveQualityCheck(QualityCheckDto dto) throws DuplicateException, SQLException {
        Optional<QualityCheck> existingCheck = qualityCheckDAO.findById(dto.getCheckId());
        if (existingCheck.isPresent()) {
            throw new DuplicateException("Quality Check ID " + dto.getCheckId() + " already exists.");
        }
        QualityCheck entity = converter.getQualityCheck(dto);
        boolean saved = qualityCheckDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save quality check record.");
        }
    }

    @Override
    public void updateQualityCheck(QualityCheckDto dto) throws NotFoundException, SQLException {
        Optional<QualityCheck> existingCheck = qualityCheckDAO.findById(dto.getCheckId());
        if (existingCheck.isEmpty()) {
            throw new NotFoundException("Quality Check ID " + dto.getCheckId() + " not found.");
        }
        QualityCheck entity = converter.getQualityCheck(dto);
        boolean updated = qualityCheckDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update quality check record.");
        }
    }

    @Override
    public boolean deleteQualityCheck(String id) throws NotFoundException, SQLException {
        Optional<QualityCheck> existingCheck = qualityCheckDAO.findById(id);
        if (existingCheck.isEmpty()) {
            throw new NotFoundException("Quality Check ID " + id + " not found.");
        }
        return qualityCheckDAO.delete(id);
    }

    @Override
    public String getNextQualityCheckId() throws SQLException {
        return qualityCheckDAO.getNextId();
    }

    @Override
    public QualityCheckDto findQualityCheckById(String id) throws SQLException {
        Optional<QualityCheck> optionalCheck = qualityCheckDAO.findById(id);
        return optionalCheck.map(converter::getQualityCheckDTO).orElse(null);
    }

    @Override
    public List<String> getAllCollectionIds() throws SQLException {
        return qualityCheckDAO.getAllMilkCollectionIds();
    }
}

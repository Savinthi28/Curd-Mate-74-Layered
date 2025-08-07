package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.QualityCheckDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface QualityCheckBO extends SuperBO {
    List<QualityCheckDto> getAllQualityChecks() throws SQLException;
    void saveQualityCheck(QualityCheckDto dto) throws DuplicateException, SQLException;
    void updateQualityCheck(QualityCheckDto dto) throws NotFoundException, SQLException;
    boolean deleteQualityCheck(String id) throws NotFoundException, SQLException;
    String getNextQualityCheckId() throws SQLException;
    QualityCheckDto findQualityCheckById(String id) throws SQLException;
    List<String> getAllCollectionIds() throws SQLException;
}

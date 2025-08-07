package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.MilkStorageDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface MilkStorageBO extends SuperBO {
    List<MilkStorageDto> getAllMilkStorages() throws SQLException;
    void saveMilkStorage(MilkStorageDto dto) throws DuplicateException, SQLException;
    void updateMilkStorage(MilkStorageDto dto) throws NotFoundException, SQLException;
    boolean deleteMilkStorage(String id) throws NotFoundException, SQLException;
    String getNextMilkStorageId() throws SQLException;
    MilkStorageDto findMilkStorageById(String id) throws SQLException;
    List<String> getAllCollectionIds() throws SQLException;
}

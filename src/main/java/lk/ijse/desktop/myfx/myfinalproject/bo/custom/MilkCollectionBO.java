package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.MilkCollectionDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface MilkCollectionBO extends SuperBO {
    List<MilkCollectionDto> getAllMilkCollections() throws SQLException;
    void saveMilkCollection(MilkCollectionDto dto) throws DuplicateException, SQLException;
    void updateMilkCollection(MilkCollectionDto dto) throws NotFoundException, SQLException;
    boolean deleteMilkCollection(String id) throws NotFoundException, SQLException;
    String getNextMilkCollectionId() throws SQLException;
    MilkCollectionDto findMilkCollectionById(String id) throws SQLException;
    List<String> getAllBuffaloIds() throws SQLException;
}

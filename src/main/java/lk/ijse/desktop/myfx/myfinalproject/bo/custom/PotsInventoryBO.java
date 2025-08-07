package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.PotsInventoryDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface PotsInventoryBO extends SuperBO {
    List<PotsInventoryDto> getAllPotsInventory() throws SQLException;
    void savePotsInventory(PotsInventoryDto dto) throws DuplicateException, SQLException;
    void updatePotsInventory(PotsInventoryDto dto) throws NotFoundException, SQLException;
    boolean deletePotsInventory(String id) throws NotFoundException, SQLException;
    String getNextPotsInventoryId() throws SQLException;
    PotsInventoryDto findPotsInventoryById(String id) throws SQLException;
    List<Integer> getAllPotsSizes() throws SQLException;
}

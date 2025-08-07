package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.RawMaterialPurchaseDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface RawMaterialPurchaseBO extends SuperBO {
    List<RawMaterialPurchaseDto> getAllRawMaterialPurchases() throws SQLException;
    void saveRawMaterialPurchase(RawMaterialPurchaseDto dto) throws DuplicateException, SQLException;
    void updateRawMaterialPurchase(RawMaterialPurchaseDto dto) throws NotFoundException, SQLException;
    boolean deleteRawMaterialPurchase(String id) throws NotFoundException, SQLException;
    String getNextRawMaterialPurchaseId() throws SQLException;
    RawMaterialPurchaseDto findRawMaterialPurchaseById(String id) throws SQLException;
    List<String> getAllSupplierIds() throws SQLException;
}

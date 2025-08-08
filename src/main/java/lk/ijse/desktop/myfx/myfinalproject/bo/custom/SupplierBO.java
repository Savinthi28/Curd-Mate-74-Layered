package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.SupplierDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface SupplierBO extends SuperBO {
    List<SupplierDto> getAllSuppliers() throws SQLException;
    void saveSupplier(SupplierDto dto) throws DuplicateException, SQLException;
    void updateSupplier(SupplierDto dto) throws NotFoundException, SQLException;
    boolean deleteSupplier(String id) throws NotFoundException, SQLException;
    String getNextSupplierId() throws SQLException;
    SupplierDto findSupplierById(String id) throws SQLException;
}

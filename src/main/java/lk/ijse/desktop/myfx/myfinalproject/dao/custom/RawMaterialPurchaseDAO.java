package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.RawMaterialPurchase;

import java.sql.SQLException;
import java.util.List;

public interface RawMaterialPurchaseDAO extends CurdDAO<RawMaterialPurchase,String> {
    List<String> getAllSupplierIds() throws SQLException;
}

package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.PotsInventory;

import java.sql.SQLException;
import java.util.List;

public interface PotsInventoryDAO extends CurdDAO<PotsInventory, String> {
    List<Integer> getAllPotsSize() throws SQLException;
}

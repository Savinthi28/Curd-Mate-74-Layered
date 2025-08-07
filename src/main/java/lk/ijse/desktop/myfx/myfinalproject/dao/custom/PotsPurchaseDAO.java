package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.PotsPurchase;

import java.sql.SQLException;
import java.util.List;

public interface PotsPurchaseDAO extends CurdDAO<PotsPurchase, String> {
    List<Integer> getAllPotsSizeFromInventory() throws SQLException;
}
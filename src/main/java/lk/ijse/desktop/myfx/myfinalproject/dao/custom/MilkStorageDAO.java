package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.MilkStorage;

import java.sql.SQLException;
import java.util.List;

public interface MilkStorageDAO extends CurdDAO<MilkStorage, String> {
    List<String> getAllCollectionIds() throws SQLException;
}

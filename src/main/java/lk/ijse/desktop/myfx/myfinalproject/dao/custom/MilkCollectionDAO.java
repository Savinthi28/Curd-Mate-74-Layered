package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.MilkCollection;

import java.sql.SQLException;
import java.util.List;

public interface MilkCollectionDAO extends CurdDAO<MilkCollection, String> {
    List<String> getAllBuffaloIds() throws SQLException;
}

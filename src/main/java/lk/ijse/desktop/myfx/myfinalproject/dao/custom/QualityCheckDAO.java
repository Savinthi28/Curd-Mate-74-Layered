package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.QualityCheck;

import java.sql.SQLException;
import java.util.List;

public interface QualityCheckDAO extends CurdDAO<QualityCheck,String> {
    List<String> getAllMilkCollectionIds() throws SQLException;
}

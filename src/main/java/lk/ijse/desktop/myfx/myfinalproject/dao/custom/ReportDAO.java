package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Report;

import java.sql.SQLException;
import java.util.List;

public interface ReportDAO extends CurdDAO<Report, String> {
    List<String> getAllUserIds() throws SQLException;
}

package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.ReportsDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface ReportBO extends SuperBO {
    List<ReportsDto> getAllReports() throws SQLException;
    void saveReport(ReportsDto dto) throws DuplicateException, SQLException;
    void updateReport(ReportsDto dto) throws NotFoundException, SQLException;
    boolean deleteReport(String id) throws NotFoundException, SQLException;
    String getNextReportId() throws SQLException;
    ReportsDto findReportById(String id) throws SQLException;
    List<String> getAllUserIds() throws SQLException;
}

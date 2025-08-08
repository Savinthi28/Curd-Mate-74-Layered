package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.ReportDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReportDAOImpl implements ReportDAO {
    @Override
    public List<Report> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Reports");
        List<Report> reports = new ArrayList<>();
        while (rs.next()) {
            reports.add(new Report(
                    rs.getString("Report_ID"),
                    rs.getString("Report_date"),
                    rs.getString("User_ID"),
                    rs.getString("Report_Type"),
                    rs.getString("Generate_By")
            ));
        }
        return reports;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Report_ID FROM Reports ORDER BY Report_ID DESC LIMIT 1");
        char tableChar = 'R';
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.substring(1);
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            return String.format(tableChar + "%03d", nextIdNumber);
        }
        return tableChar + "001";
    }

    @Override
    public boolean save(Report report) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Reports (Report_ID, Report_date, User_ID, Report_Type, Generate_By) VALUES (?, ?, ?, ?, ?)",
                report.getReportId(),
                report.getDate(),
                report.getUserId(),
                report.getReportType(),
                report.getGenerateBy()
        );
    }

    @Override
    public boolean update(Report report) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Reports SET Report_date = ?, User_ID = ?, Report_Type = ?, Generate_By = ? WHERE Report_ID = ?",
                report.getDate(),
                report.getUserId(),
                report.getReportType(),
                report.getGenerateBy(),
                report.getReportId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Reports WHERE Report_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Report_ID FROM Reports");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<Report> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Reports WHERE Report_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new Report(
                    resultSet.getString("Report_ID"),
                    resultSet.getString("Report_date"),
                    resultSet.getString("User_ID"),
                    resultSet.getString("Report_Type"),
                    resultSet.getString("Generate_By")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllUserIds() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT User_ID FROM User");
        ArrayList<String> userIds = new ArrayList<>();
        while (rs.next()) {
            userIds.add(rs.getString(1));
        }
        return userIds;
    }
}
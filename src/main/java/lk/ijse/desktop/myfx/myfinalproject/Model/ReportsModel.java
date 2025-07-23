package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.ReportsDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReportsModel {

    public static boolean updateReports(ReportsDto reportsDto) throws SQLException {
        return CrudUtil.execute(
                "update Reports set Report_date = ?, User_ID = ?, Report_Type = ?, Generate_By = ? where Report_ID = ?",
                reportsDto.getDate(),
                reportsDto.getUserId(),
                reportsDto.getReportType(),
                reportsDto.getGenerateBy(),
                reportsDto.getReportId()
        );
    }

    public static ArrayList<String> getAllUserId() throws SQLException {
        ResultSet rs = CrudUtil.execute("select DISTINCT User_ID from User");
        ArrayList<String> userIds = new ArrayList<>();
        while (rs.next()) {
            String userId = rs.getString(1);
            userIds.add(userId);
        }
        return userIds;
    }

    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select Report_ID from Reports order by Report_ID desc limit 1");
        char tableChar = 'R';
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNUmberString = lastId.substring(1);
            int lastIdNumber = Integer.parseInt(lastIdNUmberString);
            int nextIdNumber = lastIdNumber + 1;
            String nextIdString = String.format(tableChar + "%03d", nextIdNumber);
            return nextIdString;
        }
        return tableChar + "001";
    }

    public ArrayList<ReportsDto> viewAllReports() throws SQLException , ClassNotFoundException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Reports");
        ArrayList<ReportsDto> reports = new ArrayList<>();
        while (rs.next()) {
            ReportsDto reportsDto = new ReportsDto(
                    rs.getString("Report_ID"),
                    rs.getString("Report_date"),
                    rs.getString("User_ID"),
                    rs.getString("Report_Type"),
                    rs.getString("Generate_By")
            );
            reports.add(reportsDto);
        }
        return reports;
    }
    public boolean saveReport(ReportsDto reportsDto) throws SQLException , ClassNotFoundException {
        return CrudUtil.execute(
                "insert into Reports values (?,?,?,?,?)",
                reportsDto.getReportId(),
                reportsDto.getDate(),
                reportsDto.getUserId(),
                reportsDto.getReportType(),
                reportsDto.getGenerateBy()
        );
    }

    public static boolean deleteReports(String reportsDto) throws SQLException{
        String sql = "delete from Reports where Report_ID=?";
        return CrudUtil.execute(sql, reportsDto);
    }
}

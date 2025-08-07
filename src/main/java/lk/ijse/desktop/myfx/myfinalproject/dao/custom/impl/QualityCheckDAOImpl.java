package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.QualityCheckDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.QualityCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QualityCheckDAOImpl implements QualityCheckDAO {
    @Override
    public List<QualityCheck> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Quality_Check");
        List<QualityCheck> qualityChecks = new ArrayList<>();
        while (rs.next()) {
            qualityChecks.add(new QualityCheck(
                    rs.getString("Check_ID"),
                    rs.getString("Collection_ID"),
                    rs.getString("Appearance"),
                    rs.getDouble("Fat_Content"),
                    rs.getDouble("Temperature"),
                    rs.getString("Check_Date"),
                    rs.getString("Notes")
            ));
        }
        return qualityChecks;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Check_ID FROM Quality_Check ORDER BY Check_ID DESC LIMIT 1");
        char tableChar = 'Q';
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
    public boolean save(QualityCheck qualityCheck) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Quality_Check (Check_ID, Collection_ID, Appearance, Fat_Content, Temperature, Check_Date, Notes) VALUES (?, ?, ?, ?, ?, ?, ?)",
                qualityCheck.getCheckId(),
                qualityCheck.getCollectionId(),
                qualityCheck.getAppearance(),
                qualityCheck.getFatContent(),
                qualityCheck.getTemperature(),
                qualityCheck.getDate(),
                qualityCheck.getNotes()
        );
    }

    @Override
    public boolean update(QualityCheck qualityCheck) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Quality_Check SET Collection_ID = ?, Appearance = ?, Fat_Content = ?, Temperature = ?, Check_Date = ?, Notes = ? WHERE Check_ID = ?",
                qualityCheck.getCollectionId(),
                qualityCheck.getAppearance(),
                qualityCheck.getFatContent(),
                qualityCheck.getTemperature(),
                qualityCheck.getDate(),
                qualityCheck.getNotes(),
                qualityCheck.getCheckId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Quality_Check WHERE Check_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Check_ID FROM Quality_Check");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<QualityCheck> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Quality_Check WHERE Check_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new QualityCheck(
                    resultSet.getString("Check_ID"),
                    resultSet.getString("Collection_ID"),
                    resultSet.getString("Appearance"),
                    resultSet.getDouble("Fat_Content"),
                    resultSet.getDouble("Temperature"),
                    resultSet.getString("Check_Date"),
                    resultSet.getString("Notes")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllMilkCollectionIds() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT DISTINCT Collection_ID FROM Milk_Collection");
        ArrayList<String> collectionIds = new ArrayList<>();
        while (rst.next()) {
            collectionIds.add(rst.getString(1));
        }
        return collectionIds;
    }
}
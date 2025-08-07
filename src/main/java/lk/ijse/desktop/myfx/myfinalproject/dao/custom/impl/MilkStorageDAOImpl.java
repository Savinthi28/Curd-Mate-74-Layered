package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.MilkStorageDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.MilkStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MilkStorageDAOImpl implements MilkStorageDAO {
    @Override
    public List<MilkStorage> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Milk_Storage");
        List<MilkStorage> milkStorageList = new ArrayList<>();
        while (rs.next()) {
            milkStorageList.add(new MilkStorage(
                    rs.getString("Storage_ID"),
                    rs.getString("Collection_ID"),
                    rs.getString("Storage_Date"),
                    rs.getTime("Duration"),
                    rs.getDouble("Temperature")
            ));
        }
        return milkStorageList;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Storage_ID FROM Milk_Storage ORDER BY Storage_ID DESC LIMIT 1");
        String prefix = "MSI";
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.substring(prefix.length());
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            return String.format(prefix + "%03d", nextIdNumber);
        }
        return prefix + "001";
    }

    @Override
    public boolean save(MilkStorage milkStorage) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Milk_Storage (Storage_ID, Collection_ID, Storage_Date, Duration, Temperature) VALUES (?, ?, ?, ?, ?)",
                milkStorage.getStorageId(),
                milkStorage.getCollectionId(),
                milkStorage.getDate(),
                milkStorage.getDuration(),
                milkStorage.getTemperature()
        );
    }

    @Override
    public boolean update(MilkStorage milkStorage) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Milk_Storage SET Collection_ID = ?, Storage_Date = ?, Duration = ?, Temperature = ? WHERE Storage_ID = ?",
                milkStorage.getCollectionId(),
                milkStorage.getDate(),
                milkStorage.getDuration(),
                milkStorage.getTemperature(),
                milkStorage.getStorageId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Milk_Storage WHERE Storage_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Storage_ID FROM Milk_Storage");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<MilkStorage> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Milk_Storage WHERE Storage_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new MilkStorage(
                    resultSet.getString("Storage_ID"),
                    resultSet.getString("Collection_ID"),
                    resultSet.getString("Storage_Date"),
                    resultSet.getTime("Duration"),
                    resultSet.getDouble("Temperature")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllCollectionIds() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT Collection_ID FROM Milk_Collection");
        List<String> collectionIds = new ArrayList<>();
        while (rst.next()) {
            collectionIds.add(rst.getString(1));
        }
        return collectionIds;
    }
}
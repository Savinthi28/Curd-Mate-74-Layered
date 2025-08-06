package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.CurdProductionDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.CurdProduction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class CurdProductionDAOImpl implements CurdProductionDAO {
    @Override
    public List<Integer> getAllPotsSizes() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT Pots_Size FROM Pots_Inventory");
        HashSet<Integer> uniquePotsSize = new HashSet<>();
        while (rst.next()) {
            uniquePotsSize.add(rst.getInt(1));
        }
        return new ArrayList<>(uniquePotsSize);
    }

    @Override
    public List<String> getAllStorageIds() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT Storage_ID FROM Milk_Storage");
        HashSet<String> storageIds = new HashSet<>();
        while (rst.next()) {
            storageIds.add(rst.getString(1));
        }
        return new ArrayList<>(storageIds);
    }

    @Override
    public Optional<Integer> findPotsSizeById(String productionId) throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT Pots_Size FROM Curd_Production WHERE Production_ID = ?", productionId);
        if (rst.next()) {
            return Optional.of(rst.getInt("Pots_Size"));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllProductionIds() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT Production_ID FROM Curd_Production");
        List<String> list = new ArrayList<>();
        while (rst.next()) {
            list.add(rst.getString(1));
        }
        return list;
    }

    @Override
    public boolean reduceQuantity(String productionId, int quantity) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Curd_Production SET Quantity = Quantity - ? WHERE Production_ID = ?",
                quantity,
                productionId
        );
    }

    @Override
    public List<CurdProduction> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Curd_Production");
        List<CurdProduction> curdProductionList = new ArrayList<>();
        while (rs.next()) {
            curdProductionList.add(new CurdProduction(
                    rs.getString("Production_ID"),
                    rs.getDate("Production_Date").toLocalDate(),
                    rs.getDate("Expiry_Date").toLocalDate(),
                    rs.getInt("Quantity"),
                    rs.getInt("Pots_Size"),
                    rs.getString("Ingredients"),
                    rs.getString("Storage_ID")
            ));
        }
        return curdProductionList;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Production_ID FROM Curd_Production ORDER BY Production_ID DESC LIMIT 1");
        String prefix = "CP";
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
    public boolean save(CurdProduction curdProduction) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Curd_Production (Production_ID, Production_Date, Expiry_Date, Quantity, Pots_Size, Ingredients, Storage_ID) VALUES (?, ?, ?, ?, ?, ?, ?)",
                curdProduction.getProductionId(),
                curdProduction.getProductionDate(),
                curdProduction.getExpiryDate(),
                curdProduction.getQuantity(),
                curdProduction.getPotsSize(),
                curdProduction.getIngredients(),
                curdProduction.getStorageId()
        );
    }

    @Override
    public boolean update(CurdProduction curdProduction) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Curd_Production SET Production_Date = ?, Expiry_Date = ?, Quantity = ?, Pots_Size = ?, Ingredients = ?, Storage_ID = ? WHERE Production_ID = ?",
                curdProduction.getProductionDate(),
                curdProduction.getExpiryDate(),
                curdProduction.getQuantity(),
                curdProduction.getPotsSize(),
                curdProduction.getIngredients(),
                curdProduction.getStorageId(),
                curdProduction.getProductionId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Curd_Production WHERE Production_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Production_ID FROM Curd_Production");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<CurdProduction> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Curd_Production WHERE Production_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new CurdProduction(
                    resultSet.getString("Production_ID"),
                    resultSet.getDate("Production_Date").toLocalDate(),
                    resultSet.getDate("Expiry_Date").toLocalDate(),
                    resultSet.getInt("Quantity"),
                    resultSet.getInt("Pots_Size"),
                    resultSet.getString("Ingredients"),
                    resultSet.getString("Storage_ID")
            ));
        }
        return Optional.empty();
    }
}

package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.PotsInventoryDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.PotsInventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotsInventoryDAOImpl implements PotsInventoryDAO {
    @Override
    public List<PotsInventory> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Pots_Inventory");
        List<PotsInventory> potsInventories = new ArrayList<>();
        while (rs.next()) {
            potsInventories.add(new PotsInventory(
                    rs.getString("Inventory_ID"),
                    rs.getInt("Quantity"),
                    rs.getInt("Pots_Size"),
                    rs.getString("Condition")
            ));
        }
        return potsInventories;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Inventory_ID FROM Pots_Inventory ORDER BY Inventory_ID DESC LIMIT 1");
        String prefix = "PI";
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
    public boolean save(PotsInventory potsInventory) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Pots_Inventory (Inventory_ID, Quantity, Pots_Size, `Condition`) VALUES (?, ?, ?, ?)",
                potsInventory.getId(),
                potsInventory.getQuantity(),
                potsInventory.getPotsSize(),
                potsInventory.getCondition()
        );
    }

    @Override
    public boolean update(PotsInventory potsInventory) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Pots_Inventory SET Quantity = ?, Pots_Size = ?, `Condition` = ? WHERE Inventory_ID = ?",
                potsInventory.getQuantity(),
                potsInventory.getPotsSize(),
                potsInventory.getCondition(),
                potsInventory.getId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Pots_Inventory WHERE Inventory_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Inventory_ID FROM Pots_Inventory");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<PotsInventory> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Pots_Inventory WHERE Inventory_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new PotsInventory(
                    resultSet.getString("Inventory_ID"),
                    resultSet.getInt("Quantity"),
                    resultSet.getInt("Pots_Size"),
                    resultSet.getString("Condition")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<Integer> getAllPotsSize() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT DISTINCT Pots_Size FROM Pots_Inventory");
        List<Integer> potsSizes = new ArrayList<>();
        while (rs.next()) {
            potsSizes.add(rs.getInt(1));
        }
        return potsSizes;
    }
}
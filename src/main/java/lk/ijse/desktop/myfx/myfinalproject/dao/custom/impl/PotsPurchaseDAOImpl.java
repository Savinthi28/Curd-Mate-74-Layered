package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.PotsPurchaseDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.PotsPurchase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotsPurchaseDAOImpl implements PotsPurchaseDAO {
    @Override
    public List<PotsPurchase> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Pots_Purchase_ID");
        List<PotsPurchase> potsPurchases = new ArrayList<>();
        while (rs.next()) {
            potsPurchases.add(new PotsPurchase(
                    rs.getString("Purchase_ID"),
                    rs.getInt("Pots_Size"),
                    rs.getString("Purchase_Date"),
                    rs.getInt("Quantity"),
                    rs.getDouble("Unit_Price")
            ));
        }
        return potsPurchases;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Purchase_ID FROM Pots_Purchase_ID ORDER BY Purchase_ID DESC LIMIT 1");
        String prefix = "PP";
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
    public boolean save(PotsPurchase potsPurchase) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Pots_Purchase_ID (Purchase_ID, Pots_Size, Purchase_Date, Quantity, Unit_Price) VALUES (?, ?, ?, ?, ?)",
                potsPurchase.getPurchaseId(),
                potsPurchase.getPotsSize(),
                potsPurchase.getDate(),
                potsPurchase.getQuantity(),
                potsPurchase.getPrice()
        );
    }

    @Override
    public boolean update(PotsPurchase potsPurchase) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Pots_Purchase_ID SET Pots_Size = ?, Purchase_Date = ?, Quantity = ?, Unit_Price = ? WHERE Purchase_ID = ?",
                potsPurchase.getPotsSize(),
                potsPurchase.getDate(),
                potsPurchase.getQuantity(),
                potsPurchase.getPrice(),
                potsPurchase.getPurchaseId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Pots_Purchase_ID WHERE Purchase_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Purchase_ID FROM Pots_Purchase_ID");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<PotsPurchase> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Pots_Purchase_ID WHERE Purchase_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new PotsPurchase(
                    resultSet.getString("Purchase_ID"),
                    resultSet.getInt("Pots_Size"),
                    resultSet.getString("Purchase_Date"),
                    resultSet.getInt("Quantity"),
                    resultSet.getDouble("Unit_Price")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<Integer> getAllPotsSizeFromInventory() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT DISTINCT Pots_Size FROM Pots_Inventory");
        List<Integer> potsSizes = new ArrayList<>();
        while (rs.next()) {
            potsSizes.add(rs.getInt(1));
        }
        return potsSizes;
    }
}

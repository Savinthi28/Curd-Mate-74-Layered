package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.RawMaterialPurchaseDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.RawMaterialPurchase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RawMaterialPurchaseDAOImpl implements RawMaterialPurchaseDAO {
    @Override
    public List<RawMaterialPurchase> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Raw_Material_Purchase");
        List<RawMaterialPurchase> rawMaterialPurchases = new ArrayList<>();
        while (rs.next()) {
            rawMaterialPurchases.add(new RawMaterialPurchase(
                    rs.getString("Purchase_ID"),
                    rs.getString("Supplier_ID"),
                    rs.getString("Material_Name"),
                    rs.getString("Purchase_Date"),
                    rs.getInt("Quantity"),
                    rs.getDouble("Unit_Price")
            ));
        }
        return rawMaterialPurchases;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Purchase_ID FROM Raw_Material_Purchase ORDER BY Purchase_ID DESC LIMIT 1");
        String prefix = "RMP";
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
    public boolean save(RawMaterialPurchase rawMaterialPurchase) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Raw_Material_Purchase (Purchase_ID, Supplier_ID, Material_Name, Purchase_Date, Quantity, Unit_Price) VALUES (?, ?, ?, ?, ?, ?)",
                rawMaterialPurchase.getPurchaseId(),
                rawMaterialPurchase.getSupplierId(),
                rawMaterialPurchase.getMaterialName(),
                rawMaterialPurchase.getDate(),
                rawMaterialPurchase.getQuantity(),
                rawMaterialPurchase.getUnitPrice()
        );
    }

    @Override
    public boolean update(RawMaterialPurchase rawMaterialPurchase) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Raw_Material_Purchase SET Supplier_ID = ?, Material_Name = ?, Purchase_Date = ?, Quantity = ?, Unit_Price = ? WHERE Purchase_ID = ?",
                rawMaterialPurchase.getSupplierId(),
                rawMaterialPurchase.getMaterialName(),
                rawMaterialPurchase.getDate(),
                rawMaterialPurchase.getQuantity(),
                rawMaterialPurchase.getUnitPrice(),
                rawMaterialPurchase.getPurchaseId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Raw_Material_Purchase WHERE Purchase_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Purchase_ID FROM Raw_Material_Purchase");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<RawMaterialPurchase> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Raw_Material_Purchase WHERE Purchase_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new RawMaterialPurchase(
                    resultSet.getString("Purchase_ID"),
                    resultSet.getString("Supplier_ID"),
                    resultSet.getString("Material_Name"),
                    resultSet.getString("Purchase_Date"),
                    resultSet.getInt("Quantity"),
                    resultSet.getDouble("Unit_Price")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllSupplierIds() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT Supplier_ID FROM Supplier");
        ArrayList<String> supplierIds = new ArrayList<>();
        while (rs.next()) {
            supplierIds.add(rs.getString(1));
        }
        return supplierIds;
    }
}

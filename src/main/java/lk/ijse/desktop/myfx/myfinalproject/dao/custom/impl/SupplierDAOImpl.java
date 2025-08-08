package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.SupplierDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Supplier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierDAOImpl implements SupplierDAO {
    @Override
    public List<Supplier> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Supplier");
        List<Supplier> suppliers = new ArrayList<>();
        while (rs.next()) {
            suppliers.add(new Supplier(
                    rs.getString("Supplier_ID"),
                    rs.getString("Supplier_Name"),
                    rs.getString("ContactNumber"),
                    rs.getString("Address")
            ));
        }
        return suppliers;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Supplier_ID FROM Supplier ORDER BY Supplier_ID DESC LIMIT 1");
        String prefix = "SI";
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
    public boolean save(Supplier supplier) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Supplier (Supplier_ID, Supplier_Name, ContactNumber, Address) VALUES (?, ?, ?, ?)",
                supplier.getSupplierId(),
                supplier.getSupplierName(),
                supplier.getContactNumber(),
                supplier.getAddress()
        );
    }

    @Override
    public boolean update(Supplier supplier) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Supplier SET Supplier_Name = ?, ContactNumber = ?, Address = ? WHERE Supplier_ID = ?",
                supplier.getSupplierName(),
                supplier.getContactNumber(),
                supplier.getAddress(),
                supplier.getSupplierId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Supplier WHERE Supplier_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Supplier_ID FROM Supplier");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<Supplier> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Supplier WHERE Supplier_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new Supplier(
                    resultSet.getString("Supplier_ID"),
                    resultSet.getString("Supplier_Name"),
                    resultSet.getString("ContactNumber"),
                    resultSet.getString("Address")
            ));
        }
        return Optional.empty();
    }
}
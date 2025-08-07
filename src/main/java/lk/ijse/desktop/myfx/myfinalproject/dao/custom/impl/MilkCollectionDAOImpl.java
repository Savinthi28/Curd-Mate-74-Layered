package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.MilkCollectionDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.MilkCollection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MilkCollectionDAOImpl implements MilkCollectionDAO {
    @Override
    public List<MilkCollection> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Milk_Collection");
        List<MilkCollection> milkCollectionList = new ArrayList<>();
        while (rs.next()) {
            milkCollectionList.add(new MilkCollection(
                    rs.getString("Collection_ID"),
                    rs.getString("Collection_Date"),
                    rs.getDouble("Quantity"),
                    rs.getString("Buffalo_ID")
            ));
        }
        return milkCollectionList;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Collection_ID FROM Milk_Collection ORDER BY Collection_ID DESC LIMIT 1");
        String prefix = "MC";
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
    public boolean save(MilkCollection milkCollection) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Milk_Collection (Collection_ID, Collection_Date, Quantity, Buffalo_ID) VALUES (?, ?, ?, ?)",
                milkCollection.getId(),
                milkCollection.getDate(),
                milkCollection.getQuantity(),
                milkCollection.getBuffaloId()
        );
    }

    @Override
    public boolean update(MilkCollection milkCollection) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Milk_Collection SET Collection_Date = ?, Quantity = ?, Buffalo_ID = ? WHERE Collection_ID = ?",
                milkCollection.getDate(),
                milkCollection.getQuantity(),
                milkCollection.getBuffaloId(),
                milkCollection.getId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Milk_Collection WHERE Collection_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Collection_ID FROM Milk_Collection");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<MilkCollection> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Milk_Collection WHERE Collection_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new MilkCollection(
                    resultSet.getString("Collection_ID"),
                    resultSet.getString("Collection_Date"),
                    resultSet.getDouble("Quantity"),
                    resultSet.getString("Buffalo_ID")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllBuffaloIds() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT Buffalo_ID FROM Buffalo");
        List<String> list = new ArrayList<>();
        while (rst.next()) {
            list.add(rst.getString(1));
        }
        return list;
    }
}
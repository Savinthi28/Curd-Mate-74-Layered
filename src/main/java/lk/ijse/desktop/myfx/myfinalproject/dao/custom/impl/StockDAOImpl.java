package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.StockDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Stock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockDAOImpl implements StockDAO {
    @Override
    public List<Stock> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Stock");
        List<Stock> stocks = new ArrayList<>();
        while (rs.next()) {
            stocks.add(new Stock(
                    rs.getString("Stock_ID"),
                    rs.getString("Production_ID"),
                    rs.getString("Stock_Date"),
                    rs.getInt("Quantity"),
                    rs.getString("Stock_Type")
            ));
        }
        return stocks;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Stock_ID FROM Stock ORDER BY Stock_ID DESC LIMIT 1");
        char tableChar = 'S';
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
    public boolean save(Stock stock) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Stock (Stock_ID, Production_ID, Stock_Date, Quantity, Stock_Type) VALUES (?, ?, ?, ?, ?)",
                stock.getStockId(),
                stock.getProductionId(),
                stock.getDate(),
                stock.getQuantity(),
                stock.getStockType()
        );
    }

    @Override
    public boolean update(Stock stock) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Stock SET Production_ID = ?, Stock_Date = ?, Quantity = ?, Stock_Type = ? WHERE Stock_ID = ?",
                stock.getProductionId(),
                stock.getDate(),
                stock.getQuantity(),
                stock.getStockType(),
                stock.getStockId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Stock WHERE Stock_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Stock_ID FROM Stock");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<Stock> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Stock WHERE Stock_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new Stock(
                    resultSet.getString("Stock_ID"),
                    resultSet.getString("Production_ID"),
                    resultSet.getString("Stock_Date"),
                    resultSet.getInt("Quantity"),
                    resultSet.getString("Stock_Type")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllProductionIds() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT Production_ID FROM Curd_Production");
        ArrayList<String> productionIds = new ArrayList<>();
        while (rs.next()) {
            productionIds.add(rs.getString(1));
        }
        return productionIds;
    }
}
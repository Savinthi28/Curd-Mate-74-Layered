package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.StockDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockModel {

    public static boolean updateSrock(StockDto stockDto) throws SQLException {
        return CrudUtil.execute(
                "update Stock set Production_ID = ?, Stock_Date = ?, Quantity = ?, Stock_Type = ? where Stock_ID = ?",
                stockDto.getProductionId(),
                stockDto.getDate(),
                stockDto.getQuantity(),
                stockDto.getStockType(),
                stockDto.getStockId()
        );
    }

    public static ArrayList<String> getAllProductionId() throws SQLException {
        ResultSet rs = CrudUtil.execute("select Production_ID from Curd_Production");
        ArrayList<String> list = new ArrayList<>();
        while (rs.next()) {
            String productionId = rs.getString(1);
            list.add(productionId);
        }
        return list;
    }

    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select Stock_ID from Stock order by Stock_ID desc limit 1");
        char tableChar = 'S';
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

    public ArrayList<StockDto> viewAllStock() throws SQLException, ClassNotFoundException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Stock");
        ArrayList<StockDto> stocks = new ArrayList<>();
        while (rs.next()) {
            StockDto stock = new StockDto(
                    rs.getString("Stock_ID"),
                    rs.getString("Production_ID"),
                    rs.getString("Stock_Date"),
                    rs.getInt("Quantity"),
                    rs.getString("Stock_Type")
            );
            stocks.add(stock);
        }
        return stocks;
    }
    public boolean saveStock(StockDto stockDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "insert into Stock values (?,?,?,?,?)",
                stockDto.getStockId(),
                stockDto.getProductionId(),
                stockDto.getDate(),
                stockDto.getQuantity(),
                stockDto.getStockType()
        );
    }

    public boolean deleteStock(StockDto stockDto) throws SQLException {
        String sql = "delete from Stock where Stock_ID=?";
        return CrudUtil.execute(sql, stockDto.getStockId());
    }
}

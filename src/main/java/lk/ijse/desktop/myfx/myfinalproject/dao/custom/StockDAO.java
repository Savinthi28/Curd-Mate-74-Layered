package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Stock;

import java.sql.SQLException;
import java.util.List;

public interface StockDAO extends CurdDAO<Stock, String> {
    List<String> getAllProductionIds() throws SQLException;
}

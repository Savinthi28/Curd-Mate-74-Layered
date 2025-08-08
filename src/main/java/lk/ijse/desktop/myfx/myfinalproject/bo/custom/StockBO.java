package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.StockDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface StockBO extends SuperBO {
    List<StockDto> getAllStock() throws SQLException;
    void saveStock(StockDto dto) throws DuplicateException, SQLException;
    void updateStock(StockDto dto) throws NotFoundException, SQLException;
    boolean deleteStock(String id) throws NotFoundException, SQLException;
    String getNextStockId() throws SQLException;
    StockDto findStockById(String id) throws SQLException;
    List<String> getAllProductionIds() throws SQLException;
}

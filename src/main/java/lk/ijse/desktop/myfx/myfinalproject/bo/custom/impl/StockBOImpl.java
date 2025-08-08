package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.StockDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.StockBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.StockDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Stock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockBOImpl implements StockBO {
    private final StockDAO stockDAO = DAOFactory.getInstance().getDAO(DAOTypes.STOCK);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<StockDto> getAllStock() throws SQLException {
        List<Stock> entities = stockDAO.getAll();
        List<StockDto> dtos = new ArrayList<>();
        for (Stock entity : entities) {
            dtos.add(converter.getStockDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveStock(StockDto dto) throws DuplicateException, SQLException {
        Optional<Stock> existingStock = stockDAO.findById(dto.getStockId());
        if (existingStock.isPresent()) {
            throw new DuplicateException("Stock with ID " + dto.getStockId() + " already exists.");
        }
        Stock entity = converter.getStock(dto);
        boolean saved = stockDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save stock record.");
        }
    }

    @Override
    public void updateStock(StockDto dto) throws NotFoundException, SQLException {
        Optional<Stock> existingStock = stockDAO.findById(dto.getStockId());
        if (existingStock.isEmpty()) {
            throw new NotFoundException("Stock with ID " + dto.getStockId() + " not found.");
        }
        Stock entity = converter.getStock(dto);
        boolean updated = stockDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update stock record.");
        }
    }

    @Override
    public boolean deleteStock(String id) throws NotFoundException, SQLException {
        Optional<Stock> existingStock = stockDAO.findById(id);
        if (existingStock.isEmpty()) {
            throw new NotFoundException("Stock with ID " + id + " not found.");
        }
        return stockDAO.delete(id);
    }

    @Override
    public String getNextStockId() throws SQLException {
        return stockDAO.getNextId();
    }

    @Override
    public StockDto findStockById(String id) throws SQLException {
        Optional<Stock> optionalStock = stockDAO.findById(id);
        return optionalStock.map(converter::getStockDTO).orElse(null);
    }

    @Override
    public List<String> getAllProductionIds() throws SQLException {
        return stockDAO.getAllProductionIds();
    }
}
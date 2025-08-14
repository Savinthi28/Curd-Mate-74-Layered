package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.OrderDetailsDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.OrderDetails;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderDetailsDAOImpl implements OrderDetailsDAO {
    @Override
    public List<OrderDetails> getAll() throws SQLException {
        return null;
    }

    @Override
    public Optional<OrderDetails> findById(String id) throws SQLException {
        return Optional.empty();
    }


    public boolean save(OrderDetails entity, Connection connection) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Order_Details (Order_ID, Production_ID, Quantity, Unite_Price) VALUES (?,?,?,?)",
                connection,
                entity.getOrderId(),
                entity.getProductionId(),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }

    @Override
    public boolean save(OrderDetails entity) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Order_Details (Order_ID, Production_ID, Quantity, Unite_Price) VALUES (?,?,?,?)",
                entity.getOrderId(),
                entity.getProductionId(),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }

    @Override
    public boolean update(OrderDetails entity) throws SQLException {
        return false;
    }


    @Override
    public boolean delete(String id) throws SQLException {
        return false;
    }

    @Override
    public String getNextId() throws SQLException {
        return null;
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        return null;
    }

    @Override
    public boolean saveOrderDetailsList(List<OrderDetails> orderDetailsList, Connection connection) throws SQLException {
        for (OrderDetails orderDetails : orderDetailsList) {
            boolean isDetailsSaved = save(orderDetails, connection);
            if (!isDetailsSaved) {
                return false;
            }
        }
        return true;
    }
}

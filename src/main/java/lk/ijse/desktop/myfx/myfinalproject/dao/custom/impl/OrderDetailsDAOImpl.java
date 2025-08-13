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
        // Not typically used for OrderDetails directly, but implemented for SuperDAO compliance
        return null; // Or implement if needed
    }

    @Override
    public Optional<OrderDetails> findById(String id) throws SQLException {
        // OrderDetails might have composite key, or you might need a different find method
        // For simplicity, returning empty
        return Optional.empty();
    }


    public boolean save(OrderDetails entity, Connection connection) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Order_Details (Order_ID, Production_ID, Quantity, Unite_Price) VALUES (?,?,?,?)",
                connection, // Pass connection
                entity.getOrderId(),
                entity.getProductionId(),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }

    @Override
    public boolean save(OrderDetails entity) throws SQLException {
        // Should not be used for transactional saving of order details
        return SQLUtil.execute(
                "INSERT INTO Order_Details (Order_ID, Production_ID, Quantity, Unite_Price) VALUES (?,?,?,?)",
                entity.getOrderId(),
                entity.getProductionId(),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }


    public boolean update(OrderDetails entity, Connection connection) throws SQLException {
        // Not typically used for OrderDetails directly, but implemented for SuperDAO compliance
        return false; // Or implement if needed
    }

    @Override
    public boolean update(OrderDetails entity) throws SQLException {
        return false; // Or implement if needed
    }


    public boolean delete(String id, Connection connection) throws SQLException {
        // Not typically used for OrderDetails directly (composite key)
        return false; // Or implement if needed
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return false; // Or implement if needed
    }

    @Override
    public String getNextId() throws SQLException {
        // OrderDetails don't typically have a separate auto-generated ID
        return null;
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        // Not typically used for OrderDetails
        return null;
    }

    @Override
    public boolean saveOrderDetailsList(List<OrderDetails> orderDetailsList, Connection connection) throws SQLException {
        for (OrderDetails orderDetails : orderDetailsList) {
            boolean isDetailsSaved = save(orderDetails, connection); // Use transactional save
            if (!isDetailsSaved) {
                return false;
            }
            // Quantity reduction is handled in the BO layer as part of the transaction,
            // so no need to call it here.
        }
        return true;
    }
}

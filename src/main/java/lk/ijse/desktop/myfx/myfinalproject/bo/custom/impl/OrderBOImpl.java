package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.DBConnection.DBConnection;
import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDetailsDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.OrderBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.CurdProductionDAO;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.OrderDAO;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.OrderDetailsDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Order;
import lk.ijse.desktop.myfx.myfinalproject.entity.OrderDetails;

import java.sql.Connection;
import java.sql.SQLException;

public class OrderBOImpl implements OrderBO {
    private final OrderDAO orderDAO = DAOFactory.getInstance().getDAO(DAOTypes.ORDER);
    private final OrderDetailsDAO orderDetailsDAO = DAOFactory.getInstance().getDAO(DAOTypes.ORDER_DETAILS);
    private final CurdProductionDAO curdProductionDAO = DAOFactory.getInstance().getDAO(DAOTypes.CURD_PRODUCTION);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public String getNextOrderId() throws SQLException {
        return orderDAO.getNextId();
    }

    @Override
    public boolean placeOrder(OrderDto orderDto) throws SQLException {
        Connection connection = null; // Declare connection outside try-with-resources to use in catch block
        try {
            connection = DBConnection.getInstance().getConnection(); // Get connection from DBConnection
            connection.setAutoCommit(false); // Start transaction

            // 1. Save the Order (Main Order record)
            Order orderEntity = converter.getOrder(orderDto);
            boolean isOrderSaved = orderDAO.save(orderEntity); // Pass connection to DAO
            if (!isOrderSaved) {
                connection.rollback();
                return false;
            }

            // 2. Save Order Details and Update Product Quantities
            for (OrderDetailsDto detailDto : orderDto.getCartList()) {
                OrderDetails orderDetailsEntity = converter.getOrderDetails(detailDto);
                boolean isDetailsSaved = orderDetailsDAO.save(orderDetailsEntity); // Pass connection
                if (!isDetailsSaved) {
                    connection.rollback();
                    return false;
                }

                // Reduce the quantity of the curd production
                boolean isQtyReduced = curdProductionDAO.reduceQuantity(detailDto.getProductionId(), detailDto.getQuantity()); // Pass connection
                if (!isQtyReduced) {
                    connection.rollback();
                    // Optional: You can throw a more specific exception like InsufficientStockException here
                    throw new SQLException("Insufficient stock for product ID: " + detailDto.getProductionId());
                }
            }

            connection.commit(); // Commit transaction if all operations are successful
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback on any SQL exception
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            throw e; // Re-throw the original exception
        } catch (Exception e) {
            // Catch any other unexpected exceptions and rollback
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback (general exception): " + rollbackEx.getMessage());
                }
            }
            throw new SQLException("An unexpected error occurred during order placement: " + e.getMessage(), e); // Wrap and re-throw
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Always set auto-commit back to true
                    // Note: Do NOT close the connection here if it's managed by a pool or a singleton.
                    // The DBConnection.getInstance().getConnection() usually provides a shared connection.
                    // Closing it here would affect subsequent operations.
                    // If you manage connections per transaction (e.g., using a thread-local connection), then close it here.
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit or closing connection: " + e.getMessage());
                }
            }
        }
    }
}

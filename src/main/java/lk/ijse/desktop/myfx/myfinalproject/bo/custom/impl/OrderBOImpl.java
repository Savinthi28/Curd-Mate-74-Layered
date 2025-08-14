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
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            Order orderEntity = converter.getOrder(orderDto);
            boolean isOrderSaved = orderDAO.save(orderEntity);
            if (!isOrderSaved) {
                connection.rollback();
                return false;
            }

            for (OrderDetailsDto detailDto : orderDto.getCartList()) {
                OrderDetails orderDetailsEntity = converter.getOrderDetails(detailDto);
                boolean isDetailsSaved = orderDetailsDAO.save(orderDetailsEntity);
                if (!isDetailsSaved) {
                    connection.rollback();
                    return false;
                }

                boolean isQtyReduced = curdProductionDAO.reduceQuantity(detailDto.getProductionId(), detailDto.getQuantity());
                if (!isQtyReduced) {
                    connection.rollback();
                    throw new SQLException("Insufficient stock for product ID: " + detailDto.getProductionId());
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback (general exception): " + rollbackEx.getMessage());
                }
            }
            throw new SQLException("An unexpected error occurred during order placement: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit or closing connection: " + e.getMessage());
                }
            }
        }
    }
}

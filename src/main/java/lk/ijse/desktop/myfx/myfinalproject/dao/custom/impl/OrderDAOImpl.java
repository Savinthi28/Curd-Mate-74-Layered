package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.OrderDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Order;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {
    @Override
    public List<Order> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Orders");
        List<Order> orders = new ArrayList<>();
        while (rs.next()) {
            orders.add(new Order(
                    rs.getString("Order_ID"),
                    rs.getString("Customer_ID"),
                    rs.getDate("Order_Date").toLocalDate()
            ));
        }
        return orders;
    }

    @Override
    public Optional<Order> findById(String id) throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Orders WHERE Order_ID = ?", id);
        if (rs.next()) {
            return Optional.of(new Order(
                    rs.getString("Order_ID"),
                    rs.getString("Customer_ID"),
                    rs.getDate("Order_Date").toLocalDate()
            ));
        }
        return Optional.empty();
    }


//    public boolean save(Order entity, Connection connection) throws SQLException {
//        return SQLUtil.execute(
//                "INSERT INTO Orders (Order_ID, Customer_ID, Order_Date) VALUES (?,?,?)",
//                connection, // Pass connection
//                entity.getOrderId(),
//                entity.getCustomerId(),
//                entity.getOrderDate()
//        );
//    }

    @Override
    public boolean save(Order entity) throws SQLException {
        // This should primarily be used for non-transactional single saves if needed
        return SQLUtil.execute(
                "INSERT INTO Orders (Order_ID, Customer_ID, Order_Date) VALUES (?,?,?)",
                entity.getOrderId(),
                entity.getCustomerId(),
                entity.getOrderDate()
        );
    }


    public boolean update(Order entity, Connection connection) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Orders SET Customer_ID = ?, Order_Date = ? WHERE Order_ID = ?",
                connection, // Pass connection
                entity.getCustomerId(),
                entity.getOrderDate(),
                entity.getOrderId()
        );
    }

    @Override
    public boolean update(Order entity) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Orders SET Customer_ID = ?, Order_Date = ? WHERE Order_ID = ?",
                entity.getCustomerId(),
                entity.getOrderDate(),
                entity.getOrderId()
        );
    }


    public boolean delete(String id, Connection connection) throws SQLException {
        return SQLUtil.execute("DELETE FROM Orders WHERE Order_ID = ?", connection, id);
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Orders WHERE Order_ID = ?", id);
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Order_ID FROM Orders ORDER BY Order_ID DESC LIMIT 1");
        char tableChar = 'O';
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
    public List<String> getAllIds() throws SQLException {
        // Assuming you need a list of all order IDs
        ResultSet rs = SQLUtil.execute("SELECT Order_ID FROM Orders");
        List<String> orderIds = new ArrayList<>();
        while(rs.next()){
            orderIds.add(rs.getString("Order_ID"));
        }
        return orderIds;
    }
}

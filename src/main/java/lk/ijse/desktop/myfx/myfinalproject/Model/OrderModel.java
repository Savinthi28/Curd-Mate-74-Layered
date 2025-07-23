package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.DBConnection.DBConnection;
import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderModel {

    public static boolean updateOrder(OrderDto orderDto) throws SQLException {
        return CrudUtil.execute(
                "update Orders set Customer_ID = ?, Order_Date = ? where Order_ID = ?",
                orderDto.getCustomerId(),
                orderDto.getDate(),
                orderDto.getOrderId()
        );
    }

    public static ArrayList<String> getAllCustomerId() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT DISTINCT Customer_ID FROM Customer");
        ArrayList<String> customerIds = new ArrayList<>();
        while (rst.next()) {
            String customerId = rst.getString(1);
            customerIds.add(customerId);
        }
        return customerIds;
    }

    public static boolean placeOrder(OrderDto orderDto) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            boolean isSave = CrudUtil.execute(
                    "INSERT INTO Orders (Order_ID, Customer_ID, Order_Date) VALUES (?,?,?)",
                    orderDto.getOrderId(),
                    orderDto.getCustomerId(),
                    orderDto.getDate()
            );
            if (isSave) {
                boolean isDetailsSave = new OrderDetailsModel().saveOrderDetailsList(orderDto.getCartList());
                if (isDetailsSave) {
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        }catch (Exception e){
            connection.rollback();
            e.printStackTrace();
            return false;
        }finally {
            connection.setAutoCommit(true);
        }
    }



    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select Order_ID from Orders order by Order_ID desc limit 1");
        char tableChar = 'O';
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

    public ArrayList<OrderDto> viewAllOrder() throws SQLException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Orders");
        ArrayList<OrderDto> orders = new ArrayList<>();
        while (rs.next()) {
            OrderDto orderDto = new OrderDto(
                    rs.getString("Order_ID"),
                    rs.getString("Customer_ID"),
                    rs.getString("Order_Date")
            );
            orders.add(orderDto);
        }
        return orders;
    }
    public boolean saveOrder(OrderDto orderDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "insert into Orders values (?,?,?)",
                orderDto.getOrderId(),
                orderDto.getCustomerId(),
                orderDto.getDate()
        );
    }

    public boolean deleteOrder(OrderDto orderDto) throws SQLException{
        String sql = "delete from Orders where Order_ID=?";
        return CrudUtil.execute(sql,orderDto.getOrderId());
    }
}

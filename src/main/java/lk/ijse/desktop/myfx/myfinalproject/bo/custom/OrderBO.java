package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;

import java.sql.SQLException;

public interface OrderBO extends SuperBO {
    String getNextOrderId() throws SQLException;
    boolean placeOrder(OrderDto orderDto) throws SQLException;
}

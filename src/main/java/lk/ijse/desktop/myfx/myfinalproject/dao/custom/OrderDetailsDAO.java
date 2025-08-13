package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.OrderDetails;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface OrderDetailsDAO extends CurdDAO<OrderDetails,String> {
    boolean saveOrderDetailsList(List<OrderDetails> orderDetailsList, Connection connection) throws SQLException;
}

package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Payment;

import java.sql.SQLException;
import java.util.List;

public interface PaymentDAO extends CurdDAO<Payment,String> {
    List<String> getAllCustomerIds() throws SQLException;
    List<String> getAllOrderIds() throws SQLException;
    List<String> getAllPaymentMethods() throws SQLException;
}

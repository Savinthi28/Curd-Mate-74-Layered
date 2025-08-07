package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.PaymentDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface PaymentBO extends SuperBO {
    List<PaymentDto> getAllPayments() throws SQLException;
    void savePayment(PaymentDto dto) throws DuplicateException, SQLException;
    void updatePayment(PaymentDto dto) throws NotFoundException, SQLException;
    boolean deletePayment(String id) throws NotFoundException, SQLException;
    String getNextPaymentId() throws SQLException;
    PaymentDto findPaymentById(String id) throws SQLException;
    List<String> getAllCustomerIds() throws SQLException;
    List<String> getAllOrderIds() throws SQLException;
    List<String> getAllPaymentMethods() throws SQLException;
}

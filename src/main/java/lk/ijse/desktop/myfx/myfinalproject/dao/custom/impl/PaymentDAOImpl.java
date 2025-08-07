package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.PaymentDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Payment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentDAOImpl implements PaymentDAO {
    @Override
    public List<Payment> getAll() throws SQLException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM Payment");
        List<Payment> paymentList = new ArrayList<>();
        while (rs.next()) {
            paymentList.add(new Payment(
                    rs.getString("Payment_ID"),
                    rs.getString("Order_ID"),
                    rs.getString("Customer_ID"),
                    rs.getString("Payment_Date"),
                    rs.getString("Payment_Method"),
                    rs.getDouble("Payment_Amount")
            ));
        }
        return paymentList;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Payment_ID FROM Payment ORDER BY Payment_ID DESC LIMIT 1");
        char tableChar = 'P';
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
    public boolean save(Payment payment) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Payment (Payment_ID, Order_ID, Customer_ID, Payment_Date, Payment_Method, Payment_Amount) VALUES (?, ?, ?, ?, ?, ?)",
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getDate(),
                payment.getPaymentMethod(),
                payment.getAmount()
        );
    }

    @Override
    public boolean update(Payment payment) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Payment SET Order_ID = ?, Customer_ID = ?, Payment_Date = ?, Payment_Method = ?, Payment_Amount = ? WHERE Payment_ID = ?",
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getDate(),
                payment.getPaymentMethod(),
                payment.getAmount(),
                payment.getPaymentId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Payment WHERE Payment_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Payment_ID FROM Payment");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<Payment> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Payment WHERE Payment_ID = ?", id);
        if (resultSet.next()) {
            return Optional.of(new Payment(
                    resultSet.getString("Payment_ID"),
                    resultSet.getString("Order_ID"),
                    resultSet.getString("Customer_ID"),
                    resultSet.getString("Payment_Date"),
                    resultSet.getString("Payment_Method"),
                    resultSet.getDouble("Payment_Amount")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllCustomerIds() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT Customer_ID FROM Customer");
        List<String> customerIds = new ArrayList<>();
        while (rst.next()) {
            customerIds.add(rst.getString(1));
        }
        return customerIds;
    }

    @Override
    public List<String> getAllOrderIds() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT Order_ID FROM Orders");
        List<String> orderIds = new ArrayList<>();
        while (rst.next()) {
            orderIds.add(rst.getString(1));
        }
        return orderIds;
    }

    @Override
    public List<String> getAllPaymentMethods() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT DISTINCT Payment_Method FROM Payment");
        List<String> paymentMethods = new ArrayList<>();
        while (rst.next()) {
            paymentMethods.add(rst.getString(1));
        }
        return paymentMethods;
    }
}
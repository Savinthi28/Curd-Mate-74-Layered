package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.PaymentDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.PaymentBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.PaymentDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Payment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentBOImpl implements PaymentBO {

    private final PaymentDAO paymentDAO = DAOFactory.getInstance().getDAO(DAOTypes.PAYMENT);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<PaymentDto> getAllPayments() throws SQLException {
        List<Payment> entities = paymentDAO.getAll();
        List<PaymentDto> dtos = new ArrayList<>();
        for (Payment entity : entities) {
            dtos.add(converter.getPaymentDTO(entity));
        }
        return dtos;
    }

    @Override
    public void savePayment(PaymentDto dto) throws DuplicateException, SQLException {
        Optional<Payment> existingPayment = paymentDAO.findById(dto.getPaymentId());
        if (existingPayment.isPresent()) {
            throw new DuplicateException("Payment ID " + dto.getPaymentId() + " already exists.");
        }
        Payment entity = converter.getPayment(dto);
        boolean saved = paymentDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save payment record.");
        }
    }

    @Override
    public void updatePayment(PaymentDto dto) throws NotFoundException, SQLException {
        Optional<Payment> existingPayment = paymentDAO.findById(dto.getPaymentId());
        if (existingPayment.isEmpty()) {
            throw new NotFoundException("Payment ID " + dto.getPaymentId() + " not found.");
        }
        Payment entity = converter.getPayment(dto);
        boolean updated = paymentDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update payment record.");
        }
    }

    @Override
    public boolean deletePayment(String id) throws NotFoundException, SQLException {
        Optional<Payment> existingPayment = paymentDAO.findById(id);
        if (existingPayment.isEmpty()) {
            throw new NotFoundException("Payment ID " + id + " not found.");
        }
        return paymentDAO.delete(id);
    }

    @Override
    public String getNextPaymentId() throws SQLException {
        return paymentDAO.getNextId();
    }

    @Override
    public PaymentDto findPaymentById(String id) throws SQLException {
        Optional<Payment> optionalPayment = paymentDAO.findById(id);
        return optionalPayment.map(converter::getPaymentDTO).orElse(null);
    }

    @Override
    public List<String> getAllCustomerIds() throws SQLException {
        return paymentDAO.getAllCustomerIds();
    }

    @Override
    public List<String> getAllOrderIds() throws SQLException {
        return paymentDAO.getAllOrderIds();
    }

    @Override
    public List<String> getAllPaymentMethods() throws SQLException {
        return paymentDAO.getAllPaymentMethods();
    }
}

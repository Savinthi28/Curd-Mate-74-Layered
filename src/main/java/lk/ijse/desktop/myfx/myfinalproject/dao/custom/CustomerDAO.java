package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.dao.SuperDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Customer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CustomerDAO extends CurdDAO<Customer, String> {
    List<Customer> getAll() throws SQLException;

    Optional<Customer> findById(String customerId) throws SQLException;

    Optional<Customer> findCustomerByContactNumber(String customerNumber) throws SQLException;

    boolean save(Customer customer) throws SQLException;

    String getNextId() throws SQLException;

    String findCustomerNameById(String id) throws SQLException;
}

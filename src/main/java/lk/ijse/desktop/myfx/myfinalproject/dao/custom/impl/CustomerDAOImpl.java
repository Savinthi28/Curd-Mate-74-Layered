package lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.SQLUtil;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.CustomerDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAOImpl implements CustomerDAO {
    @Override
    public List<Customer> getAll() throws SQLException {
        ResultSet ts = SQLUtil.execute("SELECT * FROM Customer");

        List<Customer> list = new ArrayList<>();
        while (ts.next()) {
            Customer customer = new Customer(
                    ts.getString("Customer_ID"),
                    ts.getString("Customer_Name"),
                    ts.getString("Address"),
                    ts.getString("Customer_Number")
            );
            list.add(customer);
        }
        return list;
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findCustomerByContactNumber(String customerNumber) {
        return Optional.empty();
    }

    @Override
    public boolean save(Customer customer) throws SQLException {
        return SQLUtil.execute(
                "insert into Customer values (?,?,?,?)",
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getCustomerNumber()
        );
    }

    @Override
    public boolean update(Customer customer) throws SQLException {
        return false;
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return false;
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        return List.of();
    }

    @Override
    public String getNextId() {
        return "";
    }
}

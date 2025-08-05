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
            list.add(new Customer(
                    ts.getString("Customer_ID"),
                    ts.getString("Customer_Name"),
                    ts.getString("Address"),
                    ts.getString("Customer_Number")
            ));
        }
        return list;
    }

    @Override
    public Optional<Customer> findById(String customerId) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Customer WHERE Customer_ID = ?", customerId);
        if (resultSet.next()) {
            return Optional.of(new Customer(
                    resultSet.getString("Customer_ID"),
                    resultSet.getString("Customer_Name"),
                    resultSet.getString("Address"),
                    resultSet.getString("Customer_Number")
            ));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findCustomerByContactNumber(String customerNumber) throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM Customer WHERE Customer_Number = ?", customerNumber);
        if (resultSet.next()) {
            return Optional.of(new Customer(
                    resultSet.getString("Customer_ID"),
                    resultSet.getString("Customer_Name"),
                    resultSet.getString("Address"),
                    resultSet.getString("Customer_Number")
            ));
        }
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
        return SQLUtil.execute(
                "update Customer set Customer_Name = ?, Address = ?, Customer_Number = ? where Customer_ID = ?",
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getCustomerNumber(),
                customer.getCustomerId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtil.execute("delete from Customer where Customer_ID = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT customer_id FROM customers");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtil.execute("SELECT Customer_ID FROM Customer ORDER BY Customer_ID DESC LIMIT 1");
        char tableChar = 'C';
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
    public String findCustomerNameById(String id) throws SQLException {
        ResultSet rst = SQLUtil.execute("select Customer_Name from Customer where Customer_ID =?", id);
        if (rst.next()) {
            return rst.getString("Customer_Name");
        } else {
            return null;
        }
    }
}

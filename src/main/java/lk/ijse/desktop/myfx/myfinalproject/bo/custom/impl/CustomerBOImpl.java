package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.CustomerBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.InUseException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.CustomerDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerBOImpl implements CustomerBO {

    private final CustomerDAO customerDAO = DAOFactory.getInstance().getDAO(DAOTypes.CUSTOMER);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<CustomerDto> getAllCustomer() throws SQLException {
        List<Customer> customers = customerDAO.getAll();
        List<CustomerDto> customerDtos = new ArrayList<>();
        for (Customer customer : customers) {
            customerDtos.add(converter.getCustomerDTO(customer));
        }
        return customerDtos;
    }

    @Override
    public void saveCustomer(CustomerDto dto) throws DuplicateException, SQLException {
        Optional<Customer> optionalCustomer = customerDAO.findById(dto.getCustomerId());
        if (optionalCustomer.isPresent()) {
            throw new DuplicateException("Customer ID " + dto.getCustomerId() + " already exists.");
        }
        Optional<Customer> customerByContactOptional = customerDAO.findCustomerByContactNumber(dto.getCustomerNumber());
        if (customerByContactOptional.isPresent()) {
            throw new DuplicateException("Customer Number " + dto.getCustomerNumber() + " already exists.");
        }
        Customer customer = converter.getCustomer(dto);
        boolean saved = customerDAO.save(customer);
        if (!saved) {
            throw new SQLException("Failed to save customer record.");
        }
    }

    @Override
    public void updateCustomer(CustomerDto dto) throws NotFoundException, SQLException {
        Optional<Customer> optionalCustomer = customerDAO.findById(dto.getCustomerId());
        if (optionalCustomer.isEmpty()) {
            throw new NotFoundException("Customer ID " + dto.getCustomerId() + " not found.");
        }
        Customer customer = converter.getCustomer(dto);
        boolean updated = customerDAO.update(customer);
        if (!updated) {
            throw new SQLException("Failed to update customer record.");
        }
    }

    @Override
    public boolean deleteCustomer(String id) throws NotFoundException, InUseException, SQLException {
        Optional<Customer> optionalCustomer = customerDAO.findById(id);
        if (optionalCustomer.isEmpty()) {
            throw new NotFoundException("Customer ID " + id + " not found.");
        }
        try {
            return customerDAO.delete(id);
        } catch (SQLException e) {
            if (e.getMessage().contains("Cannot delete or update a parent row: a foreign key constraint fails")) {
                throw new InUseException("Customer ID " + id + " is linked to other records (e.g., orders) and cannot be deleted.");
            }
            throw new SQLException("Error deleting customer record: " + e.getMessage(), e);
        }
    }


    @Override
    public String getNextId() throws SQLException {
        return customerDAO.getNextId();
    }

    @Override
    public CustomerDto findCustomerById(String id) throws SQLException {
        Optional<Customer> optionalCustomer = customerDAO.findById(id);
        return optionalCustomer.map(converter::getCustomerDTO).orElse(null);
    }

    @Override
    public String findCustomerNameById(String id) throws SQLException {
        return customerDAO.findCustomerNameById(id);
    }
}

package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.CustomerBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
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

    private final CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getInstance().getDAO(DAOTypes.CUSTOMER);
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
    public void saveCustomer(CustomerDto dto) throws SQLException {
        Optional<Customer> optionalCustomer = customerDAO.findById(dto.getCustomerId());
        if (optionalCustomer.isPresent()) {
            throw new DuplicateException("Duplicate Customer ID");
        }
        Optional<Customer> customerByContactOptional = customerDAO.findCustomerByContactNumber(dto.getCustomerNumber());
        if (customerByContactOptional.isPresent()) {
            throw new DuplicateException("Duplicate Customer Number");
        }
        Customer customer = converter.getCustomer(dto);
        customerDAO.save(customer);
    }

    @Override
    public void updateCustomer(CustomerDto dto) throws SQLException {

    }

    @Override
    public void deleteCustomer(CustomerDto dto) throws SQLException {

    }

    @Override
    public String getNextId() throws SQLException {
        return customerDAO.getNextId();
    }
}

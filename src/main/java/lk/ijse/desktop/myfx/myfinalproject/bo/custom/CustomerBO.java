package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;

import java.sql.SQLException;
import java.util.List;

public interface CustomerBO extends SuperBO {
    List<CustomerDto> getAllCustomer() throws SQLException;
    void saveCustomer(CustomerDto dto) throws SQLException;
    void updateCustomer(CustomerDto dto) throws SQLException;
    boolean deleteCustomer(String id) throws SQLException;
    String getNextId() throws SQLException;
    CustomerDto findCustomerById(String id) throws SQLException;
    String findCustomerNameById(String id) throws SQLException;
}

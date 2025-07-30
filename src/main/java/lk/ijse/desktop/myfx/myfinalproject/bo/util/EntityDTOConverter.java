package lk.ijse.desktop.myfx.myfinalproject.bo.util;

import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.entity.Customer;

public class EntityDTOConverter {
    public Customer getCustomer(CustomerDto dto) {
        return new Customer(
                dto.getCustomerId(),
                dto.getCustomerName(),
                dto.getAddress(),
                dto.getCustomerNumber()
        );
    }

    public CustomerDto getCustomerDTO(Customer customer) {
        return new CustomerDto(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getCustomerNumber()
        );
    }
}

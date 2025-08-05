package lk.ijse.desktop.myfx.myfinalproject.bo.util;

import lk.ijse.desktop.myfx.myfinalproject.Dto.BuffaloDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.entity.Buffalo;
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
    public Buffalo getBuffalo(BuffaloDto dto) {
        return new Buffalo(
                dto.getBuffaloID(),
                dto.getMilkProduction(),
                dto.getGender(),
                dto.getAge(),
                dto.getHealthStatus()
        );
    }
    public BuffaloDto getBuffaloDTO(Buffalo buffalo) {
        return new BuffaloDto(
                buffalo.getBuffaloId(),
                buffalo.getMilkProduction(),
                buffalo.getGender(),
                buffalo.getAge(),
                buffalo.getHealthStatus()
        );
    }

}

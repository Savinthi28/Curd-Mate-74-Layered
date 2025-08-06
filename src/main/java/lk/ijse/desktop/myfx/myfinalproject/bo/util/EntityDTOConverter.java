package lk.ijse.desktop.myfx.myfinalproject.bo.util;

import lk.ijse.desktop.myfx.myfinalproject.Dto.BuffaloDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.CurdProductionDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.entity.Buffalo;
import lk.ijse.desktop.myfx.myfinalproject.entity.CurdProduction;
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
    public CurdProduction getCurdProduction(CurdProductionDto dto) {
        return new CurdProduction(
                dto.getProductionId(),
                dto.getProductionDate(),
                dto.getExpiryDate(),
                dto.getQuantity(),
                dto.getPotsSize(),
                dto.getIngredients(),
                dto.getStorageId()
        );
    }
    public CurdProductionDto getCurdProductionDTO(CurdProduction curdProduction) {
        return new CurdProductionDto(
                curdProduction.getProductionId(),
                curdProduction.getProductionDate(),
                curdProduction.getExpiryDate(),
                curdProduction.getQuantity(),
                curdProduction.getPotsSize(),
                curdProduction.getIngredients(),
                curdProduction.getStorageId()
        );
    }
}

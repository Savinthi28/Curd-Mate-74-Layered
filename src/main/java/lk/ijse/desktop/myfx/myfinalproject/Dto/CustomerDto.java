package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class CustomerDto {
    private String customerId;
    private String customerName;
    private String address;
    private String customerNumber;

    public CustomerDto(String id) {
        this.customerId = id;
    }
}

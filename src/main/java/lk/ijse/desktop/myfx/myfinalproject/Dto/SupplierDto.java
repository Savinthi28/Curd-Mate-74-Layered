package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class SupplierDto {
    private String supplierId;
    private String supplierName;
    private String contactNumber;
    private String address;

    public SupplierDto(String id) {
        this.supplierId = id;
    }
}

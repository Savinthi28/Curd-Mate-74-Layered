package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class RawMaterialPurchaseDto {
    private String purchaseId;
    private String supplierId;
    private String materialName;
    private String Date;
    private int quantity;
    private double unitPrice;

    public RawMaterialPurchaseDto(String id) {
        this.purchaseId = id;
    }
}

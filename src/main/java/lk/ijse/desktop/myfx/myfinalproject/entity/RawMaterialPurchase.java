package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RawMaterialPurchase {
    private String purchaseId;
    private String supplierId;
    private String materialName;
    private String Date;
    private int quantity;
    private double unitPrice;
}

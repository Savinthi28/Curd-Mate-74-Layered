package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PotsPurchase {
    private String purchaseId;
    private int potsSize;
    private String date;
    private int quantity;
    private double price;
}

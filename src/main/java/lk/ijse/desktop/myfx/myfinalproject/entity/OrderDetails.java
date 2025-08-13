package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetails {
    private String orderId;
    private String productionId;
    private int quantity;
    private double unitPrice;
}

package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment {
    private String paymentId;
    private String orderId;
    private String customerId;
    private String date;
    private String paymentMethod;
    private double amount;
}

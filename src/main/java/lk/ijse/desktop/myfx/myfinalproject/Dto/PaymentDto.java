package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class PaymentDto {
    private String paymentId;
    private String orderId;
    private String customerId;
    private String date;
    private String paymentMethod;
    private double amount;

    public PaymentDto(String id) {
        this.paymentId = id;
    }
}

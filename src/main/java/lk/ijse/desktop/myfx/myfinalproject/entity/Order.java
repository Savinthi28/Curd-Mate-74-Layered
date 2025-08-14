package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    private String orderId;
    private String customerId;
    private LocalDate orderDate;
}
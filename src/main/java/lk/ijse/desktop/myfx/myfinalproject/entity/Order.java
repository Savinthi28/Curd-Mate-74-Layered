package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate; // LocalDate එක import කරගන්න ඕනේ

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    private String orderId;
    private String customerId;
    private LocalDate orderDate; // Data එක LocalDate විදියට වෙනස් කරන්න
}
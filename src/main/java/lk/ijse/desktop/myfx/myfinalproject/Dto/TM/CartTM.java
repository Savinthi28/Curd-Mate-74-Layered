package lk.ijse.desktop.myfx.myfinalproject.Dto.TM;

import javafx.scene.control.Button;
import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class CartTM {
    private String productionId;
    private String potsSize;
    private int qty;
    private double unitPrice;
    private double totalPrice;
    private Button btnRemove;
}

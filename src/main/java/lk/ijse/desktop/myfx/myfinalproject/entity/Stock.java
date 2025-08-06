package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Stock {
    private String stockId;
    private String productionId;
    private String date;
    private int quantity;
    private String stockType;
}

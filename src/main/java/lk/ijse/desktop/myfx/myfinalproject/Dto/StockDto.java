package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class StockDto {
    private String stockId;
    private String productionId;
    private String date;
    private int quantity;
    private String stockType;

    public StockDto(String stockId) {
        this.stockId = stockId;
    }
}

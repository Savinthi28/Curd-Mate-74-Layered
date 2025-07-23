package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class DailyIncomeDto {
    private String id;
    private String customerName;
    private String date;
    private String description;
    private double amount;

    public DailyIncomeDto(String id) {
        this.id = id;
    }
}

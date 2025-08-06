package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyExpense {
    private String id;
    private String date;
    private String description;
    private double amount;
    private boolean dailyExpense;
}

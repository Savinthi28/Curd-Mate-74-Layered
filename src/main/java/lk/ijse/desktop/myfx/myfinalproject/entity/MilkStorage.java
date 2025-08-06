package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MilkStorage {
    private String storageId;
    private String collectionId;
    private String date;
    private Time duration;
    private double temperature;
}

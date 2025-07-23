package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

import java.sql.Time;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class MilkStorageDto {
    private String storageId;
    private String collectionId;
    private String date;
    private Time duration;
    private double temperature;

    public MilkStorageDto(String id, Integer value, String text, Time duration, double temperature) {
        this.storageId = id;
    }
}

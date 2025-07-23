package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class QualityCheckDto {
    private String checkId;
    private String collectionId;
    private String appearance;
    private double fatContent;
    private double temperature;
    private String date;
    private String notes;

    public QualityCheckDto(String id) {
        this.checkId = id;
    }
}

package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QualityCheck {
    private String checkId;
    private String collectionId;
    private String appearance;
    private double fatContent;
    private double temperature;
    private String date;
    private String notes;
}

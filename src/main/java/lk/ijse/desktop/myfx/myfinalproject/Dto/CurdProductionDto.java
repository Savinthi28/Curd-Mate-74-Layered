package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class CurdProductionDto {
    private String productionId;
    private String productionDate;
    private String expiryDate;
    private int quantity;
    private int potsSize;
    private String ingredients;
    private String storageId;

    public CurdProductionDto(String id) {
        this.productionId = id;
    }
}

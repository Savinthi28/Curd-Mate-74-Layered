package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class CurdProductionDto {
    private String productionId;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private int quantity;
    private int potsSize;
    private String ingredients;
    private String storageId;

}

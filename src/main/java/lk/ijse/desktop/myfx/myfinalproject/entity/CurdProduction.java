package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurdProduction {
    private String productionId;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private int quantity;
    private int potsSize;
    private String ingredients;
    private String storageId;
}

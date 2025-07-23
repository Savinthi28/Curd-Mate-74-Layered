package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class PotsPurchaseDto {
    private String purchaseId;
    private int potsSize;
    private String date;
    private int quantity;
    private double price;

    public PotsPurchaseDto(String id) {
        this.purchaseId = id;
    }
}

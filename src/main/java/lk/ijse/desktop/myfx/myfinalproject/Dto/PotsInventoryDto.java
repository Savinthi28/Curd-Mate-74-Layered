package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class PotsInventoryDto {
    private String id;
    private int quantity;
    private int potsSize;
    private String condition;

    public PotsInventoryDto(String id) {
        this.id = id;
    }
}

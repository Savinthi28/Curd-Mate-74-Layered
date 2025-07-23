package lk.ijse.desktop.myfx.myfinalproject.Dto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class BuffaloDto {
    private String buffaloID;
    private double milkProduction;
    private String gender;
    private int age;
    private String healthStatus;

    public BuffaloDto(String id) {
        this.buffaloID = id;
    }
}

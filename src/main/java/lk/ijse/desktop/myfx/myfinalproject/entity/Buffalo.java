package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Buffalo {
    private String buffaloId;
    private double milkProduction;
    private String gender;
    private int age;
    private String healthStatus;
}

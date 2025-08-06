package lk.ijse.desktop.myfx.myfinalproject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Report {
    private String reportId;
    private String date;
    private String userId;
    private String reportType;
    private String generateBy;
}

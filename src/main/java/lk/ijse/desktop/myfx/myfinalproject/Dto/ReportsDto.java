package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class ReportsDto {
    private String reportId;
    private String date;
    private String userId;
    private String reportType;
    private String generateBy;

    public ReportsDto(String reportId) {
        this.reportId = reportId;
    }
}

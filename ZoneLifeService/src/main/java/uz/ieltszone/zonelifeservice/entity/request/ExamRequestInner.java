package uz.ieltszone.zonelifeservice.entity.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamRequestInner {
    private Long excelFileId;
    private List<ResultRequest> resultResponses;
}
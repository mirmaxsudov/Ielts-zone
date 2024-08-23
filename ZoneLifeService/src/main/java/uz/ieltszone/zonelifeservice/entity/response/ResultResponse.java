package uz.ieltszone.zonelifeservice.entity.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    private Double writing;
    private Double reading;
    private Double listening;
    private Double speaking;
    private Double total;
}
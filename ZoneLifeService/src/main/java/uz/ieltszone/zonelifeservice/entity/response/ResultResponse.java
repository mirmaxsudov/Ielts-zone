package uz.ieltszone.zonelifeservice.entity.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    private Float writing;
    private Float reading;
    private Float listening;
    private Float speaking;
    private Float total;
}
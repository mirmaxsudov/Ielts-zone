package uz.ieltszone.zonelifeservice.entity.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResultRequest {
    private Float total;
    private Float writing;
    private Float reading;
    private Float listening;
    private Float speaking;
}
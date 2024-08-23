package uz.ieltszone.zonelifeservice.entity.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultRequest {
    private Float total;
    private Float writing;
    private Float reading;
    private Float listening;
    private Float speaking;
}
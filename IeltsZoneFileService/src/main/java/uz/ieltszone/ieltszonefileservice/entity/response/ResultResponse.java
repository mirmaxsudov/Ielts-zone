package uz.ieltszone.ieltszonefileservice.entity.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    private Float writing;
    private Float reading;
    private Float listening;
    private Float speaking;
    private Float total;
}
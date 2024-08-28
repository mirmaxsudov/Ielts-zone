package uz.ieltszone.zonelifeservice.entity.payload;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TotalSums {
    private Float listeningTotal;
    private Float readingTotal;
    private Float speakingTotal;
    private Float writingTotal;
}

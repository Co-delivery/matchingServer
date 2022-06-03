package project.Codelivery.dto.Match;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MatchAlarmDto {
    private String userId;

    public MatchAlarmDto(String userId) {
        this.userId = userId;
    }
}
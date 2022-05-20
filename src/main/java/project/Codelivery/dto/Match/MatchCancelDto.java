package project.Codelivery.dto.Match;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MatchCancelDto {
    private String userId;

    public MatchCancelDto(MatchResponseDto matchResponseDto) {
        this.userId = matchResponseDto.getUserId();
    }

}
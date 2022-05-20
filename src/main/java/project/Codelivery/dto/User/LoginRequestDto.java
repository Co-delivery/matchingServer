package project.Codelivery.dto.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.Codelivery.dto.Match.MatchResponseDto;

@Setter
@Getter
@NoArgsConstructor
public class LoginRequestDto {
    private String userId;
    private String password;
    private String token;

    public LoginRequestDto(MatchResponseDto matchResponseDto) {
        this.userId = matchResponseDto.getUserId();
    }
}


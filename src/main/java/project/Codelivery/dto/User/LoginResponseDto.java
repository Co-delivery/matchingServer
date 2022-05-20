package project.Codelivery.dto.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.Codelivery.domain.User.User;

@Setter
@Getter
@NoArgsConstructor
public class LoginResponseDto {
    private String nickname;
    private String address;

    @Builder
    public LoginResponseDto(User user) {
        this.nickname = user.getNickname();
        this.address = user.getAddress();
    }
}

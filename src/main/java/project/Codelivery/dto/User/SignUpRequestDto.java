package project.Codelivery.dto.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SignUpRequestDto {
    private String userId;
    private String password;
    private String address;
    private String token;
    private String nickname;
}

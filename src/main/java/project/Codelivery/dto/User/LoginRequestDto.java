package project.Codelivery.dto.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LoginRequestDto {
    private String userId;
    private String password;
    private String token;
}


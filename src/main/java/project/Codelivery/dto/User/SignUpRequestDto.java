package project.Codelivery.dto.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.Codelivery.domain.User.User;

@Setter
@Getter
@NoArgsConstructor
public class SignUpRequestDto {

    private String userId;
    private String password;
    private String address;
    private String token;
    private String nickname;

    @Builder
    public SignUpRequestDto(String userId, String password, String address, String token, String nickname) {
        this.userId = userId;
        this.password = password;
        this.address = address;
        this.token = token;
        this.nickname = nickname;
    }
    
    public User toEntity() {
        return User.builder()
                .userId(userId)
                .password(password)
                .address(address)
                .token(token)
                .nickname(nickname)
                .build();
    }
}

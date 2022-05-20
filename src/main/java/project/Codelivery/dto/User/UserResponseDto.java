package project.Codelivery.dto.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.Codelivery.domain.User.User;

@Setter
@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String Id;
    private String password;

    @Builder
    public UserResponseDto(User user) {
        this.Id = user.getUserId();
        this.password = user.getPassword();
    }
}

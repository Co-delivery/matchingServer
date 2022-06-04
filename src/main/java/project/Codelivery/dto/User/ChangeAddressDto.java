package project.Codelivery.dto.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ChangeAddressDto {
    String userId;
    String address;
}

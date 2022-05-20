package project.Codelivery.dto.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrdersRequestDto {
    private String userId;

    public OrdersRequestDto(OrdersResponseDto ordersResponseDto) {
        this.userId = ordersResponseDto.getUserId();
    }
}
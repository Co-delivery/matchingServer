package project.Codelivery.dto.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class OrdersResponseDto {
    private String userId;
    private String restaurant;
    private int price;
    private List<String> item;

    @Builder
    public OrdersResponseDto(String user_id, String restaurant, int price, List<String> item) {
        this.userId = user_id;
        this.restaurant = restaurant;
        this.price = price;
        this.item = item;
    }
}

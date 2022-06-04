package project.Codelivery.dto.Match;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class MatchRequestDto {
    private String userId;
    private String address;
    private String restaurant;
    private int menu_price;
    private int delivery_price;
    private List<String> item;
    private double latitude;
    private double longitude;
    private int location;

    @Builder
    public MatchRequestDto(String userId, String address, String restaurant, int menu_price, int delivery_price, List<String> item, int location) {
        this.userId = userId;
        this.address = address;
        this.restaurant = restaurant;
        this.menu_price = menu_price;
        this.delivery_price = delivery_price;
        this.item = item;
        this.location = location;
    }
}

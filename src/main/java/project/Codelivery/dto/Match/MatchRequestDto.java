package project.Codelivery.dto.Match;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.Codelivery.domain.Queue.Queue;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class MatchRequestDto {
    private String userId;
    private String address;
    private String restaurant;
    private int price;
    private List<String> item;
    private double latitude;
    private double longitude;

    @Builder
    public MatchRequestDto(String userId, String address, String restaurant, int price, List<String> item) {
        this.userId = userId;
        this.address = address;
        this.restaurant = restaurant;
        this.price = price;
        this.item = item;
    }


    public Queue toEntity() {
        return Queue.builder()
                .userId(userId)
                .longitude(longitude)
                .latitude(latitude)
                .restaurant(restaurant)
                .build();
    }
}

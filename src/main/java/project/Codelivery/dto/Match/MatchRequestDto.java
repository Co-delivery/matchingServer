package project.Codelivery.dto.Match;

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
}

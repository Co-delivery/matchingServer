package project.Codelivery.dto.Match;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.Codelivery.domain.Queue.Queue;

@Setter
@Getter
@NoArgsConstructor
public class MatchResponseDto {
    private String userId;
    private String restaurant;


    @Builder
    public MatchResponseDto(Queue queue) {
        this.userId = queue.getUserId();
        this.restaurant = queue.getRestaurant();
    }
}
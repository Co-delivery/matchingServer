package project.Codelivery.dto.Match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MatchAcceptRequestDto {
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private String to;
        private Data data;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data{
        private String title;
        private String message;

        private int matchId;
        private int user_num;
        private String other_nickname;
        private String other_address;
        private int my_price;
        private int other_price;
        private int delivery_price;
    }
}

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
        private String token;
        private Notification notification;
        private Data data;
    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification{
        private String title;
        private String body;
    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data{
        private String event;
        private int matchId;
        private int user_num;
        private String other_nickname;
        private double my_latitude;
        private double my_longitude;
        private double other_latitude;
        private double other_longitude;
        private int my_price;
        private int other_price;
        private int delivery_price;
    }
}

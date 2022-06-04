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
        private String matchId;
        private String user_num;
        private String other_nickname;
        private String my_latitude;
        private String my_longitude;
        private String other_latitude;
        private String other_longitude;
        private String my_price;
        private String other_price;
        private String delivery_price;
    }
}

package project.Codelivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.Codelivery.domain.Messages;
import project.Codelivery.dto.Alarm.MessageAlarmDto;
import project.Codelivery.dto.Match.MatchAcceptRequestDto;
import project.Codelivery.service.FCMService;

import java.nio.charset.Charset;

@RequiredArgsConstructor
@RestController
public class FcmController {
    private final FCMService fcmService;

    @GetMapping("/test")
    public void test() throws Exception {
        MatchAcceptRequestDto.Data data = MatchAcceptRequestDto.Data.builder()
                .event("match success")
                .build();
        fcmService.sendMessageTo("eril55cYtkM8rq9XTVV6rB:APA91bHXHQVl4n-txLQq0NFPCUlqcLJyZ3zn2CXevwVXoS04SDds5Ds7gKYc_B77uOhKuNYuz0kUxBj-7BN4HmrAVHdV1omcHUCK8FB8hQdNRaUv2eAqmrDNVDWFmxNlj-WOYrF9JCSO",
                "매칭성공",
                "매칭이 성공하였습니다.",
                data);
    }

    @PostMapping("/alarm/message")
    public ResponseEntity<Messages> messageAlarm(@RequestBody MessageAlarmDto requestDto){
        System.out.println("***************/n/n/n/n/n/n/n/n" + requestDto.getChatMessageId() + "/n/n/n/n/n/n/n******************");
        Messages messages = Messages.builder()
                .httpStatus(200)
                .message("Matching registered successfully")
                .data(null)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, headers, HttpStatus.OK);
    }
}
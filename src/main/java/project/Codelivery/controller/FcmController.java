package project.Codelivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.Codelivery.dto.Alarm.MessageAlarmDto;
import project.Codelivery.service.FCMService;

@RequiredArgsConstructor
@RestController
public class FcmController {
    private final FCMService fcmService;

    @PostMapping("/alarm/message")
    public void messageAlarm(@RequestBody MessageAlarmDto requestDto){
        int chatMessageId = requestDto.getChatMessageId();

    }
}
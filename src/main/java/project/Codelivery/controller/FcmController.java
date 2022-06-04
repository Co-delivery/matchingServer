package project.Codelivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.Codelivery.domain.Messages;
import project.Codelivery.dto.Alarm.MessageAlarmDto;
import project.Codelivery.service.MatchService;

import java.nio.charset.Charset;

@RequiredArgsConstructor
@RestController
public class FcmController {
    private final MatchService matchService;

    @PostMapping("/alarm/message")
    public ResponseEntity<Messages> messageAlarm(@RequestBody MessageAlarmDto requestDto) throws Exception {
        matchService.sendMessageAlarm(requestDto.getChatMessageId());
        Messages messages = Messages.builder()
                .httpStatus(200)
                .message("Message push success")
                .data(requestDto)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, headers, HttpStatus.OK);
    }
}
package project.Codelivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.Codelivery.domain.Messages;
import project.Codelivery.dto.Match.*;
import project.Codelivery.service.MatchService;
import java.nio.charset.Charset;

@RequiredArgsConstructor
@RestController
public class MatchController {
    private final MatchService matchService;

    @PostMapping("/match/request")
    public ResponseEntity<Messages> matchRequest(@RequestBody MatchRequestDto requestDto) {
        String user_id = matchService.save(requestDto);
        Messages messages = Messages.builder()
                .httpStatus(200)
                .message("Matching registered successfully")
                .data(requestDto)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, headers, HttpStatus.OK);
    }

    @DeleteMapping("/match/cancel")
    public ResponseEntity<Messages> matchCancel(@RequestBody MatchCancelDto requestDto) {
        String deleted_user_id = matchService.delete(requestDto.getUserId());
        Messages messages = Messages.builder()
                .httpStatus(200)
                .message("Delete user with id : " + deleted_user_id)
                .data(deleted_user_id)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, headers, HttpStatus.OK);
    }

    @PostMapping("/match/accept")
    public ResponseEntity<Messages> matchAccept(@RequestBody MatchAcceptResponseDto requestDto) {
        matchService.matchAcceptResponse(requestDto);
        Messages messages = Messages.builder()
                .httpStatus(200)
                .message("match response complete")
                .data(requestDto)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, headers, HttpStatus.OK);
    }

    @PostMapping("/match/payment")
    public ResponseEntity<Messages> matchPayment(@RequestBody MatchAcceptResponseDto requestDto) {
        matchService.matchPaymentResponse(requestDto);
        Messages messages = Messages.builder()
                .httpStatus(200)
                .message("payment complete")
                .data(requestDto)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, headers, HttpStatus.OK);
    }

}
package project.Codelivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.Codelivery.domain.Messages;
import project.Codelivery.domain.User.User;
import project.Codelivery.domain.User.UserRepository;
import project.Codelivery.dto.User.*;
import project.Codelivery.service.UserService;

import java.nio.charset.Charset;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/user/signup")
    public ResponseEntity<Messages> signUpRequest(@RequestBody SignUpRequestDto requestDto) {
        String user_id = userService.save(requestDto);
        UserResponseDto responseDto = userService.findById(user_id);
        Messages messages = Messages.builder()
                .httpStatus(200)
                .message("Sign up successfully")
                .data(responseDto)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, headers, HttpStatus.OK);
    }

    @PostMapping("/user/login")
    public ResponseEntity<Messages> LogInRequest(@RequestBody LoginRequestDto requestDto) {
        String user_id = requestDto.getUserId();
        Messages messages;
        User user = userService.updateToken(user_id, requestDto.getToken());
        LoginResponseDto responseDto = new LoginResponseDto(user);
        if (userService.findById(user_id).getPassword().equals(requestDto.getPassword())){
            messages = Messages.builder()
                    .httpStatus(200)
                    .message("Login successful")
                    .data(responseDto)
                    .build();
        }
        else {
            messages = Messages.builder()
                    .httpStatus(403)
                    .message("Password mismatch")
                    .data(requestDto)
                    .build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, headers, HttpStatus.OK);
    }

    //@PostMapping("/user/order")
    //public ResponseEntity<Messages> OrdersRequest(@RequestBody OrdersRequestDto requestDto) {
    //    String user_id = requestDto.getUserId();
    //    OrdersResponseDto responseDto = userService.findOrders(user_id);
    //    Messages messages = Messages.builder()
    //            .httpStatus(200)
    //            .message("order check successful")
    //            .data(responseDto)
    //            .build();
    //    HttpHeaders headers = new HttpHeaders();
    //    headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
    //    return new ResponseEntity<>(messages, headers, HttpStatus.OK);
    //}
}

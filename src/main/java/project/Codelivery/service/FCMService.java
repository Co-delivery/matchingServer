package project.Codelivery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import project.Codelivery.dto.Match.MatchAcceptRequestDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {
    private final String API_URL = "https://fcm.googleapis.com/fcm/send";
    private final ObjectMapper objectMapper;

    public void sendMessageTo(String token, MatchAcceptRequestDto.Data data) throws Exception {
        String message = makeMessage(token, data);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "key=AAAAbTlFuvs:APA91bHJ1izIQAIj8GEVAN3zQlzsQyb92xraKnWIZHZkmC0GOiXDWxVOw1Lwa4A1wRQZdSiM92fTY_8Nho0XxarPu4SxGei_Ovsa_gKvwf5VsRflEjfS-tIyjyNm4GPsfNbtPO55vvXT")
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    private String makeMessage(String token, MatchAcceptRequestDto.Data data) throws JsonProcessingException {
        MatchAcceptRequestDto fcmMessage = MatchAcceptRequestDto.builder()
                .message(MatchAcceptRequestDto.Message.builder().to(token).data(data).build())
                .build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws Exception {
        String firebaseConfigPath = "fireBase/codelivery-fdee0-firebase-adminsdk-7awyi-4b1dd8cf8f.json";

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        System.out.println("token : " + googleCredentials.getAccessToken().getTokenValue());
        return googleCredentials.getAccessToken().getTokenValue();
    }
}

package project.Codelivery.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import project.Codelivery.dto.Match.MatchAcceptRequestDto;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/codelivery-fdee0/messages:send";
    private final ObjectMapper objectMapper;

    public void sendMessageTo(String token, String title, String body, MatchAcceptRequestDto.Data data) throws Exception {
        String message = makeMessage(token, title, body, data);

        System.out.println(message);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();


        System.out.println(response.code());
        System.out.println(response);
        System.out.println(request);
        System.out.println();

    }

    private String makeMessage(String token,String title, String body, MatchAcceptRequestDto.Data data) throws JsonProcessingException {
        MatchAcceptRequestDto fcmMessage = MatchAcceptRequestDto.builder()
                .message(MatchAcceptRequestDto.Message.builder()
                        .notification(MatchAcceptRequestDto.Notification.builder().title(title).body(body).build())
                        .data(data)
                        .token(token)
                        .build()
                )
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

    private void sendMessageAlarm(String chatMessageId){

    }
}

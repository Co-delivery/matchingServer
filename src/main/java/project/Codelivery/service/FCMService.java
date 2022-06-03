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
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer" + getAccessToken())
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
        String firebaseConfigPath = "key_path";

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}

package project.Codelivery.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import project.Codelivery.domain.ChatMessage.ChatMessage;
import project.Codelivery.domain.ChatMessage.ChatMessageRepository;
import project.Codelivery.domain.ChatRoomJoin.ChatRoomJoinRepository;
import project.Codelivery.domain.MatchResult.MatchResult;
import project.Codelivery.domain.MatchResult.MatchResultRepository;
import project.Codelivery.domain.Orders.Orders;
import project.Codelivery.domain.Orders.OrdersRepository;
import project.Codelivery.domain.OrderList.OrderList;
import project.Codelivery.domain.OrderList.OrderListRepository;
import project.Codelivery.domain.Queue.Queue;
import project.Codelivery.domain.Queue.QueueRepository;
import project.Codelivery.domain.User.UserRepository;
import project.Codelivery.dto.Match.MatchAcceptRequestDto;
import project.Codelivery.dto.Match.MatchAcceptResponseDto;
import project.Codelivery.dto.Match.MatchRequestDto;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MatchService {

    private final QueueRepository queueRepository;
    private final OrderListRepository orderListRepository;
    private final OrdersRepository ordersRepository;
    private final MatchResultRepository matchResultRepository;
    private final FCMService fcmService;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomJoinRepository chatRoomJoinRepository;

    @Transactional
    public String save(MatchRequestDto requestDto) throws IllegalArgumentException{
        try {
            String address = requestDto.getAddress();

            String addr = "https://dapi.kakao.com/v2/local/search/address.json";
            String apiKey = "KakaoAK 5a1d5c0b5b41dfba335ea72619d76ee3";

            address = URLEncoder.encode(address, "UTF-8");
            String query = "query=" + address;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(addr);
            stringBuffer.append("?");
            stringBuffer.append(query);

            System.out.println("stringBuffer.toString() "+ stringBuffer.toString());

            URL url = new URL(stringBuffer.toString());
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Authorization", apiKey);
            BufferedReader rd = null;
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            StringBuffer docJson = new StringBuffer();
            String line;

            while((line=rd.readLine())!=null){
                docJson.append(line);
            }

            if(0<docJson.toString().length()){
                System.out.println("docJson    :"+docJson.toString());

            }
            rd.close();

            JSONObject jsonObject = new JSONObject(docJson.toString());
            JSONArray jsonArray= (JSONArray) jsonObject.get("documents");
            JSONObject tempObj = (JSONObject) jsonArray.get(0);

            requestDto.setLatitude(tempObj.getDouble("y"));
            requestDto.setLongitude(tempObj.getDouble("x"));

            System.out.println("latitude : " + tempObj.getDouble("y"));
            System.out.println("longitude : " + tempObj.getDouble("x"));

        }catch(Exception e) {
            e.printStackTrace();
        }
        // 위도경도변환 Geocoding
        // https://blog.naver.com/PostView.nhn?blogId=slayra&logNo=221383891512&from=search&redirect=Log&widgetTypeCall=true&directAccess=false

        double latitude = requestDto.getLatitude();
        double longitude = requestDto.getLongitude();
        int location = 1;
        if(37.281439 < latitude && latitude <= 37.284644) { location += 0; }
        if(37.278235 < latitude && latitude <= 37.281439) { location += 3; }
        if(37.275031 <= latitude && latitude <= 37.278235) { location += 6; }
        if(127.037537 <= longitude && longitude <= 127.041653) { location += 0; }
        if(127.041653 < longitude && longitude <= 127.045769) { location += 1; }
        if(127.045769 < longitude && longitude <= 127.049885) { location += 2; }
        if(longitude < 127.037537 || longitude > 127.049885 || latitude > 37.284644 || latitude < 37.275031) {
            throw new IllegalArgumentException(" : Not a service area.");
        }
        requestDto.setLocation(location);

        Queue queue = Queue.builder()
                .restaurant(requestDto.getRestaurant())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .location(requestDto.getLocation())
                .userId(requestDto.getUserId())
                .build();
        String Id = queueRepository.save(queue).getUserId();

        Orders order = Orders.builder()
                .userId(requestDto.getUserId())
                .restaurant(requestDto.getRestaurant())
                .menu_price(requestDto.getMenu_price())
                .delivery_price(requestDto.getDelivery_price())
                .build();
        ordersRepository.save(order);

        for( String s : requestDto.getItem()) {
            OrderList orderList = OrderList.builder()
                    .orderId(ordersRepository.findByUserId(Id).get().getOrderId())
                    .item(s)
                    .build();
            orderListRepository.save(orderList);
        }
        return Id;
    }


    @Transactional
    public String delete (String user_id) {
        Queue queue = queueRepository.findByUserId(user_id).orElseThrow(
                ()->new IllegalArgumentException("Error raise at usersRepository.findById, "+user_id)
        );
        queueRepository.delete(queue);

        Orders orders = ordersRepository.findByUserId(user_id).orElseThrow(
                ()->new IllegalArgumentException("Error raise at usersRepository.findById, "+user_id)
        );
        ordersRepository.delete(orders);

        return user_id;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void matching() throws Exception {

            //매칭 시간 초과
            List<String> expiredList = queueRepository.findQueueIdByTimeStamp();
            for(String e : expiredList){
                String userId = queueRepository.findByQueueId(Integer.parseInt(e)).getUserId();
                String token = userRepository.findByUserId(userId).getToken();
                queueRepository.deleteByQueueId(Integer.parseInt(e));
                ordersRepository.deleteByUserId(userId);
                MatchAcceptRequestDto.Data data = MatchAcceptRequestDto.Data.builder()
                                                                            .event("matching cancel")
                                                                            .build();
                fcmService.sendMessageTo(token, "매칭시간 초과", "상대방을 찾을 수 없습니다.", data);
            }

            //매칭
            List<String> restaurantList = queueRepository.findRestaurant();
            for (String r : restaurantList) {
                List<String> locationList = queueRepository.findLocationByRestaurant(r);
                for (String l : locationList) {
                    List<String> queueIdList = queueRepository.findQueueIdByRestaurantAndLocation(r, l);
                    int length= queueIdList.size();
                    for(int i=0; i<length-1; i++){
                        String queueId1 = queueIdList.get(i);
                        if(queueRepository.findByQueueId(Integer.parseInt(queueId1)).getState()==0) {
                            for(int j=i+1;j<length;j++){
                                String queueId2 = queueIdList.get(j);
                                MatchResult matchResult = MatchResult.builder()
                                        .user1(Integer.parseInt(queueId1))
                                        .user2(Integer.parseInt(queueId2))
                                        .build();
                                if(queueRepository.findByQueueId(Integer.parseInt(queueId2)).getState()==0 && !matchResultRepository.existsByMatchId(matchResult.getMatchId())){
                                    int matchId = matchResultRepository.save(matchResult).getMatchId();
                                    Queue queue1 = queueRepository.findByQueueId(Integer.parseInt(queueId1));
                                    queue1.setState(1);
                                    Queue queue2 = queueRepository.findByQueueId(Integer.parseInt(queueId2));
                                    queue2.setState(1);
                                    String token1 = userRepository.findOneByUserId(queue1.getUserId()).get().getToken();
                                    String token2 = userRepository.findOneByUserId(queue2.getUserId()).get().getToken();
                                    MatchAcceptRequestDto.Data data1 = MatchAcceptRequestDto.Data.builder()
                                            .event("find match")
                                            .matchId(String.valueOf(matchId))
                                            .user_num(String.valueOf(1))
                                            .other_nickname(String.valueOf(userRepository.findOneByUserId(queue2.getUserId()).get().getNickname()))
                                            .my_latitude(String.valueOf(queue1.getLatitude()))
                                            .my_longitude(String.valueOf(queue1.getLongitude()))
                                            .other_latitude(String.valueOf(queue2.getLatitude()))
                                            .other_longitude(String.valueOf(queue2.getLongitude()))
                                            .other_price(String.valueOf(ordersRepository.findByUserId(queue2.getUserId()).get().getMenu_price()))
                                            .my_price(String.valueOf(ordersRepository.findByUserId(queue1.getUserId()).get().getMenu_price()))
                                            .delivery_price(String.valueOf(ordersRepository.findByUserId(queue1.getUserId()).get().getDelivery_price()))
                                            .build();
                                    MatchAcceptRequestDto.Data data2 = MatchAcceptRequestDto.Data.builder()
                                            .event("find match")
                                            .matchId(String.valueOf(matchId))
                                            .user_num(String.valueOf(2))
                                            .other_nickname(userRepository.findOneByUserId(queue1.getUserId()).get().getNickname())
                                            .my_latitude(String.valueOf(queue2.getLatitude()))
                                            .my_longitude(String.valueOf(queue2.getLongitude()))
                                            .other_latitude(String.valueOf(queue1.getLatitude()))
                                            .other_longitude(String.valueOf(queue1.getLongitude()))
                                            .other_price(String.valueOf(ordersRepository.findByUserId(queue1.getUserId()).get().getMenu_price()))
                                            .my_price(String.valueOf(ordersRepository.findByUserId(queue2.getUserId()).get().getMenu_price()))
                                            .delivery_price(String.valueOf(ordersRepository.findByUserId(queue2.getUserId()).get().getDelivery_price()))
                                            .build();
                                    fcmService.sendMessageTo(token1, "매칭 완료", "상대방을 확인해주세요.", data1);
                                    fcmService.sendMessageTo(token2, "매칭 완료", "상대방을 확인해주세요.", data2);
                                    break;
                                }
                            }
                        }
                    }
                    queueIdList.clear();
                }
                locationList.clear();
            }
            restaurantList.clear();
    }

    @Transactional
    public void matchAcceptResponse(MatchAcceptResponseDto requestDto){
        int matchId = requestDto.getMatchId();
        int user_num = requestDto.getUser_num();
        int result = requestDto.getResult();
        if(!matchResultRepository.existsByMatchId(matchId)){
            throw new IllegalArgumentException(" : invalid matchId.");
        }

        MatchResult matchResult = matchResultRepository.findByMatchId(matchId);
        if(user_num==1){
            matchResult.setUser1_result(result);
        }
        if(user_num==2){
            matchResult.setUser2_result(result);
        }
    }

    @Transactional
    public void matchPaymentResponse(MatchAcceptResponseDto requestDto){
        int matchId = requestDto.getMatchId();
        int user_num = requestDto.getUser_num();
        int result = requestDto.getResult();
        if(!matchResultRepository.existsByMatchId(matchId)){
            throw new IllegalArgumentException(" : invalid matchId.");
        }

        MatchResult matchResult = matchResultRepository.findByMatchId(matchId);
        if(user_num==1){
            matchResult.setUser1_pay_result(result);
        }
        if(user_num==2){
            matchResult.setUser2_pay_result(result);
        }
    }


    @Transactional
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void matchResultCheck() throws Exception {

        //실패 CASE
        List<String> failedList = matchResultRepository.findFailedMatchId();
        for(String f : failedList) {
            matchResultRepository.findByMatchId(Integer.parseInt(f)).setState(1);
            Queue queue1 = queueRepository.findByQueueId(matchResultRepository.findByMatchId(Integer.parseInt(f)).getUser1()); queue1.setState(0);
            Queue queue2 = queueRepository.findByQueueId(matchResultRepository.findByMatchId(Integer.parseInt(f)).getUser2()); queue2.setState(0);
            String token1 = userRepository.findOneByUserId(queue1.getUserId()).get().getToken();
            String token2 = userRepository.findOneByUserId(queue2.getUserId()).get().getToken();
            MatchAcceptRequestDto.Data data = MatchAcceptRequestDto.Data.builder()
                    .event("match fail")
                    .build();
            fcmService.sendMessageTo(token1,"매칭 실패", "매칭이 성사되지 않았어요.", data);
            fcmService.sendMessageTo(token2, "매칭 실패", "매칭이 성사되지 않았어요.", data);
        }

        //수락 성공 CASE
        List<String> successList = matchResultRepository.findSuccessMatchId();
        for(String s : successList) {
            MatchResult matchResult = matchResultRepository.findByMatchId(Integer.parseInt(s));
            matchResult.setState(2);
            Queue queue1 = queueRepository.findByQueueId(matchResult.getUser1());
            Queue queue2 = queueRepository.findByQueueId(matchResult.getUser2());
            String token1 = userRepository.findOneByUserId(queue1.getUserId()).get().getToken();
            String token2 = userRepository.findOneByUserId(queue2.getUserId()).get().getToken();
            MatchAcceptRequestDto.Data data1 = MatchAcceptRequestDto.Data.builder()
                    .event("payment required")
                    .build();
            MatchAcceptRequestDto.Data data2 = MatchAcceptRequestDto.Data.builder()
                    .event("payment required")
                    .build();
            fcmService.sendMessageTo(token1, "결제 요청", "상대방이 수락하였습니다. 결제를 완료해주세요.", data1);
            fcmService.sendMessageTo(token2, "결제 요청", "상대방이 수락하였습니다. 결제를 완료해주세요.", data2);
        }

        //결제 성공 CASE
        List<String> completeList = matchResultRepository.findCompleteMatchId();
        for(String s : completeList) {
            MatchResult matchResult = matchResultRepository.findByMatchId(Integer.parseInt(s));
            Queue queue1 = queueRepository.findByQueueId(matchResult.getUser1());
            Queue queue2 = queueRepository.findByQueueId(matchResult.getUser2());
            String token1 = userRepository.findOneByUserId(queue1.getUserId()).get().getToken();
            String token2 = userRepository.findOneByUserId(queue2.getUserId()).get().getToken();
            MatchAcceptRequestDto.Data data1 = MatchAcceptRequestDto.Data.builder()
                    .event("match success")
                    .other_nickname(userRepository.findOneByUserId(queue2.getUserId()).get().getNickname())
                    .my_latitude(String.valueOf(queue1.getLatitude()))
                    .my_longitude(String.valueOf(queue1.getLongitude()))
                    .other_latitude(String.valueOf(queue2.getLatitude()))
                    .other_longitude(String.valueOf(queue2.getLongitude()))
                    .other_price(String.valueOf(ordersRepository.findByUserId(queue2.getUserId()).get().getMenu_price()))
                    .my_price(String.valueOf(ordersRepository.findByUserId(queue1.getUserId()).get().getMenu_price()))
                    .delivery_price(String.valueOf(ordersRepository.findByUserId(queue1.getUserId()).get().getDelivery_price()))
                    .build();
            MatchAcceptRequestDto.Data data2 = MatchAcceptRequestDto.Data.builder()
                    .event("match success")
                    .other_nickname(userRepository.findOneByUserId(queue1.getUserId()).get().getNickname())
                    .my_latitude(String.valueOf(queue2.getLatitude()))
                    .my_longitude(String.valueOf(queue2.getLongitude()))
                    .other_latitude(String.valueOf(queue1.getLatitude()))
                    .other_longitude(String.valueOf(queue1.getLongitude()))
                    .other_price(String.valueOf(ordersRepository.findByUserId(queue1.getUserId()).get().getMenu_price()))
                    .my_price(String.valueOf(ordersRepository.findByUserId(queue2.getUserId()).get().getMenu_price()))
                    .delivery_price(String.valueOf(ordersRepository.findByUserId(queue2.getUserId()).get().getDelivery_price()))
                    .build();
            fcmService.sendMessageTo(token1, "매칭 성공", "매칭이 성공하였습니다.", data1);
            fcmService.sendMessageTo(token2, "매칭 성공", "매칭이 성공하였습니다.", data2);
            queueRepository.delete(queue1);
            queueRepository.delete(queue2);
            ordersRepository.deleteByUserId(queue1.getUserId());
            ordersRepository.deleteByUserId(queue2.getUserId());
        }
    }

    public void sendMessageAlarm(int chatMessageId) throws Exception {
        ChatMessage chatMessage = chatMessageRepository.findByMessageId(chatMessageId);
        String message = chatMessage.getMessage();
        String roomId = chatMessage.getRoomId();
        String senderId = chatMessage.getUserId();
        String receiverId;
        if(senderId.equals(chatRoomJoinRepository.findByRoomId(roomId).getUserId1())){
            receiverId = chatRoomJoinRepository.findByRoomId(roomId).getUserId2();
        }
        else{
            receiverId = chatRoomJoinRepository.findByRoomId(roomId).getUserId1();
        }
        String token = userRepository.findOneByUserId(receiverId).get().getToken();;
        MatchAcceptRequestDto.Data data = MatchAcceptRequestDto.Data.builder().event("new chat message").build();
        fcmService.sendMessageTo(token, "채팅 도착", message, data);
    }
}


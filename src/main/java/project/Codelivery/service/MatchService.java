package project.Codelivery.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
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
import project.Codelivery.dto.Match.MatchResponseDto;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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

    @Transactional
    public String save(MatchRequestDto requestDto) {

        // 위도경도 변환
        // 출처 : https://blog.naver.com/PostView.nhn?blogId=slayra&logNo=221383891512&from=search&redirect=Log&widgetTypeCall=true&directAccess=false
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

            double latitude = tempObj.getDouble("y");
            double longitude = tempObj.getDouble("x");
            int location = 1;
            if(37.281439 < latitude && latitude <= 37.284644) { location += 0; }
            if(37.278235 < latitude && latitude <= 37.281439) { location += 3; }
            if(37.275031 <= latitude && latitude <= 37.278235) { location += 6; }
            if(127.037537 <= longitude && longitude <= 127.041653) { location += 0; }
            if(127.041653 < longitude && longitude <= 127.045769) { location += 1; }
            if(127.045769 < longitude && longitude <= 127.049885) { location += 2; }
            if(longitude < 127.037537 || longitude > 127.049885 || latitude > 37.284644 || latitude < 37.275031) {
                throw new IllegalArgumentException("Not a service location.");
            }
            requestDto.setLocation(location);
        }catch(Exception e) {
            e.printStackTrace();
        }

        Queue queue = Queue.builder()
                .restaurant(requestDto.getRestaurant())
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

    public MatchResponseDto findByUserId(String user_id) {
        Queue entity = queueRepository.findByUserId(user_id).orElseThrow(
                ()->new IllegalArgumentException("Error raise at usersRepository.findById, "+ user_id)
         );
        return new MatchResponseDto(entity);
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
    public void matching() throws Exception {

            List<String> expiredList = queueRepository.findQueueIdByTimeStamp();
            for(String e : expiredList){
                String userId = queueRepository.findByQueueId(Integer.parseInt(e)).getUserId();
                String token = userRepository.findByUserId(userId).getToken();
                queueRepository.deleteByQueueId(Integer.parseInt(e));
                ordersRepository.deleteByUserId(userId);
                //send message to user "time over"
                MatchAcceptRequestDto.Data data = MatchAcceptRequestDto.Data.builder()
                                                                            .title("매칭 취소")
                                                                            .message("상대방을 찾는데 실패했어요.")
                                                                            .build();
                //fcmService.sendMessageTo(token, data);
            }


            List<String> restaurantList = queueRepository.findRestaurant();
            for (String r : restaurantList) {
                List<String> locationList = queueRepository.findLocationByRestaurant(r);
                for (String l : locationList) {
                    List<String> queueIdList = queueRepository.findQueueIdByRestaurantAndLocation(r, l);
                    int length= queueIdList.size();
                        for(int i=0; i+1<length; i+=2){
                            String queueId1 = queueIdList.get(i);
                            String queueId2 = queueIdList.get(i+1);
                            MatchResult matchResult = MatchResult.builder()
                                    .user1(Integer.parseInt(queueId1))
                                    .user2(Integer.parseInt(queueId2))
                                    .build();
                            if(matchResultRepository.existsByMatchId(matchResult.getMatchId())){ break; }
                            int matchId = matchResultRepository.save(matchResult).getMatchId();
                            Queue queue1 = queueRepository.findByQueueId(Integer.parseInt(queueId1)); queue1.setState(1);
                            Queue queue2 = queueRepository.findByQueueId(Integer.parseInt(queueId2)); queue2.setState(1);
                            //send message to user1 & user2 "match accept?"
                            String token1 = userRepository.findOneByUserId(queue1.getUserId()).get().getToken();
                            String token2 = userRepository.findOneByUserId(queue2.getUserId()).get().getToken();
                            MatchAcceptRequestDto.Data data1 = MatchAcceptRequestDto.Data.builder()
                                    .title("매칭 완료")
                                    .message("상대방을 확인해주세요.")
                                    .matchId(matchId)
                                    .user_num(1)
                                    .other_nickname(userRepository.findOneByUserId(queue2.getUserId()).get().getNickname())
                                    .other_address(userRepository.findOneByUserId(queue2.getUserId()).get().getAddress())
                                    .other_price(ordersRepository.findByUserId(queue2.getUserId()).get().getMenu_price())
                                    .my_price(ordersRepository.findByUserId(queue1.getUserId()).get().getMenu_price())
                                    .delivery_price(ordersRepository.findByUserId(queue1.getUserId()).get().getDelivery_price())
                                    .build();
                            MatchAcceptRequestDto.Data data2 = MatchAcceptRequestDto.Data.builder()
                                    .title("매칭 완료")
                                    .message("상대방을 확인해주세요.")
                                    .matchId(matchId)
                                    .user_num(2)
                                    .other_nickname(userRepository.findOneByUserId(queue1.getUserId()).get().getNickname())
                                    .other_address(userRepository.findOneByUserId(queue1.getUserId()).get().getAddress())
                                    .other_price(ordersRepository.findByUserId(queue1.getUserId()).get().getMenu_price())
                                    .my_price(ordersRepository.findByUserId(queue2.getUserId()).get().getMenu_price())
                                    .delivery_price(ordersRepository.findByUserId(queue2.getUserId()).get().getDelivery_price())
                                    .build();
                            //fcmService.sendMessageTo(token1, data1);
                            //fcmService.sendMessageTo(token2, data2);
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

        MatchResult matchResult = matchResultRepository.findByMatchId(matchId);
        if(user_num==1){
            matchResult.setUser1_result(result);
        }
        if(user_num==2){
            matchResult.setUser2_result(result);
        }
    }


    @Transactional
    public void matchResultCheck() throws Exception {
        List<String> failedList = matchResultRepository.findFailedMatchId();
        for(String f : failedList) {
            Queue queue1 = queueRepository.findByQueueId(matchResultRepository.findByMatchId(Integer.parseInt(f)).getUser1()); queue1.setState(0);
            Queue queue2 = queueRepository.findByQueueId(matchResultRepository.findByMatchId(Integer.parseInt(f)).getUser2()); queue2.setState(0);
            //send message to user1, user2 "matching failed."
            String token1 = userRepository.findOneByUserId(queue1.getUserId()).get().getToken();
            String token2 = userRepository.findOneByUserId(queue2.getUserId()).get().getToken();
            MatchAcceptRequestDto.Data data = MatchAcceptRequestDto.Data.builder()
                    .title("매칭 실패")
                    .message("매칭이 성사되지 않았어요.")
                    .build();
            //fcmService.sendMessageTo(token1, data);
            //fcmService.sendMessageTo(token2, data);
        }

        List<String> successList = matchResultRepository.findSuccessMatchId();
        for(String s : successList) {
            MatchResult matchResult = matchResultRepository.findByMatchId(Integer.parseInt(s));
            Queue queue1 = queueRepository.findByQueueId(matchResult.getUser1());
            Queue queue2 = queueRepository.findByQueueId(matchResult.getUser2());
            //send message to user1, user2 "matching success."
            String token1 = userRepository.findOneByUserId(queue1.getUserId()).get().getToken();
            String token2 = userRepository.findOneByUserId(queue2.getUserId()).get().getToken();
            MatchAcceptRequestDto.Data data1 = MatchAcceptRequestDto.Data.builder()
                    .title("매칭 성공")
                    .message("매칭이 성사되었어요.")
                    .other_nickname(userRepository.findOneByUserId(queue2.getUserId()).get().getNickname())
                    .other_address(userRepository.findOneByUserId(queue2.getUserId()).get().getAddress())
                    .other_price(ordersRepository.findByUserId(queue2.getUserId()).get().getMenu_price())
                    .my_price(ordersRepository.findByUserId(queue1.getUserId()).get().getMenu_price())
                    .delivery_price(ordersRepository.findByUserId(queue1.getUserId()).get().getDelivery_price())
                    .build();
            MatchAcceptRequestDto.Data data2 = MatchAcceptRequestDto.Data.builder()
                    .title("매칭 성공")
                    .message("매칭이 성사되었어요.")
                    .other_nickname(userRepository.findOneByUserId(queue1.getUserId()).get().getNickname())
                    .other_address(userRepository.findOneByUserId(queue1.getUserId()).get().getAddress())
                    .other_price(ordersRepository.findByUserId(queue1.getUserId()).get().getMenu_price())
                    .my_price(ordersRepository.findByUserId(queue2.getUserId()).get().getMenu_price())
                    .delivery_price(ordersRepository.findByUserId(queue2.getUserId()).get().getDelivery_price())
                    .build();
            //fcmService.sendMessageTo(token1, data1);
            //fcmService.sendMessageTo(token2, data2);
            queueRepository.delete(queue1);
            queueRepository.delete(queue2);
            ordersRepository.deleteByUserId(queue1.getUserId());
            ordersRepository.deleteByUserId(queue2.getUserId());
        }
    }
}


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
import project.Codelivery.dto.Match.MatchRequestDto;
import project.Codelivery.dto.Match.MatchResponseDto;

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

    @Transactional
    public String save(MatchRequestDto requestDto) {

        // 위도경도 변환
        // 출처 : https://blog.naver.com/PostView.nhn?blogId=slayra&logNo=221383891512&from=search&redirect=Log&widgetTypeCall=true&directAccess=false
        try {

            String location = requestDto.getAddress();

            String addr = "https://dapi.kakao.com/v2/local/search/address.json";

            String apiKey = "KakaoAK 5a1d5c0b5b41dfba335ea72619d76ee3";

            location = URLEncoder.encode(location, "UTF-8");

            String query = "query=" + location;

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

        Queue queue = Queue.builder()
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .restaurant(requestDto.getRestaurant())
                .userId(requestDto.getUserId())
                .build();
        String Id = queueRepository.save(queue).getUserId();

        Orders order = Orders.builder()
                .userId(requestDto.getUserId())
                .restaurant(requestDto.getRestaurant())
                .price(requestDto.getPrice())
                .build();
        ordersRepository.save(order);

        for( String s : requestDto.getItem()) {
            OrderList orderList = OrderList.builder()
                    .orderId(ordersRepository.findByUserId(Id).get().getOrderId())
                    .item(s)
                    .build();
            System.out.println("orderitem : " + orderList);
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
    public String matchCheck(MatchRequestDto RequestDto){
        List<Queue> queueList = queueRepository.findAll();
        String user_id = RequestDto.getUserId();
        for(Queue q : queueList){
            double lon1 = q.getLongitude();
            double lat1 = q.getLatitude();
            double lon2 = RequestDto.getLongitude();
            double lat2 = RequestDto.getLatitude();
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515 * 1609.344;
            if( dist <= 500 && !user_id.equals(q.getUserId())){
                MatchResult matchResult = MatchResult.builder()
                        .user1(q.getQueueId())
                        .user2(queueRepository.findByUserId(user_id).get().getQueueId())
                        .build();
                matchResultRepository.save(matchResult);
                //queueRepository.delete(q);
                //Queue queue = queueRepository.findByUserId(user_id).orElseThrow(
                //        ()->new IllegalArgumentException("Error raise at usersRepository.findById, "+user_id)
                //);
                //queueRepository.delete(queue);
                System.out.println("*********Matching User Found**********");
                return user_id + q.getUserId();
            }
        }
        System.out.println("*********No Matching User**********");
        return null;
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) { return (rad * 180 / Math.PI);}
}


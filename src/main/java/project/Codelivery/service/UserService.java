package project.Codelivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.Codelivery.domain.OrderList.OrderList;
import project.Codelivery.domain.OrderList.OrderListRepository;
import project.Codelivery.domain.Orders.Orders;
import project.Codelivery.domain.Orders.OrdersRepository;
import project.Codelivery.domain.Queue.Queue;
import project.Codelivery.domain.User.User;
import project.Codelivery.domain.User.UserRepository;
import project.Codelivery.dto.User.OrdersResponseDto;
import project.Codelivery.dto.User.SignUpRequestDto;
import project.Codelivery.dto.User.UserResponseDto;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final OrdersRepository ordersRepository;
    private final OrderListRepository orderListRepository;

    @Transactional
    public String save(SignUpRequestDto requestDto) {
        User user = User.builder()
                .userId(requestDto.getUserId())
                .password(requestDto.getPassword())
                .address(requestDto.getAddress())
                .token(requestDto.getToken())
                .nickname(requestDto.getNickname())
                .build();
        String Id = userRepository.save(user).getUserId();
        return Id;
    }

    public UserResponseDto findById(String user_id) {
        User entity = userRepository.findById(user_id).orElseThrow(
                ()->new IllegalArgumentException("ID not found : "+ user_id)
        );
        return new UserResponseDto(entity);
    }

    @Transactional
    public User updateToken(String user_id, String token){
        User findresult = userRepository.findOneByUserId(user_id).get();
        findresult.setToken(token);
        return findresult;
    }

    public OrdersResponseDto findOrders(String user_id){
        Orders orders = ordersRepository.findByUserId(user_id).get();
        int price = orders.getPrice();
        String restaurant = orders.getRestaurant();
        String orderId = orders.getOrderId();
        List<String> item = new ArrayList<>();

        List<OrderList> orderListList = orderListRepository.findAllByOrderId(orderId);
        for( OrderList o : orderListList){
            item.add(o.getItem());
        }
        OrdersResponseDto responseDto = new OrdersResponseDto(user_id, restaurant, price, item);
        return responseDto;
    }
}

package project.Codelivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.Codelivery.domain.OrderList.OrderList;
import project.Codelivery.domain.OrderList.OrderListRepository;
import project.Codelivery.domain.Orders.Orders;
import project.Codelivery.domain.Orders.OrdersRepository;
import project.Codelivery.domain.User.User;
import project.Codelivery.domain.User.UserRepository;
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
        if(userRepository.existsById(requestDto.getUserId())){
            throw new IllegalArgumentException(" : userId already exists.");
        }
        if(userRepository.existsByNickname(requestDto.getNickname())){
            throw new IllegalArgumentException(" : nickname already exists.");
        }
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

    @Transactional
    public User updateAddress(String user_id, String address){
        User findresult = userRepository.findOneByUserId(user_id).get();
        findresult.setAddress(address);
        return findresult;
    }

}

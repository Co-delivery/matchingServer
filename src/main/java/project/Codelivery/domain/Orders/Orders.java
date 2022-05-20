package project.Codelivery.domain.Orders;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.Codelivery.domain.BaseTimeEntity;
import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table (name = "Orders")
public class Orders extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private String orderId;

    @Column
    private String restaurant;
    private int price;

    @Column(name = "User_Id")
    private String userId;

    @Builder
    public Orders(String restaurant, int price, String userId) {
        this.restaurant = restaurant;
        this.price = price;
        this.userId = userId;
    }
}

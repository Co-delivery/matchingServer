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
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private String orderId;

    @Column
    private String restaurant;
    private int menu_price;
    private int delivery_price;
    private int price;

    @Column(name = "User_Id")
    private String userId;

    @Builder
    public Orders(String restaurant, int menu_price, int delivery_price, String userId) {
        this.restaurant = restaurant;
        this.menu_price = menu_price;
        this.delivery_price = delivery_price;
        this.price = menu_price + delivery_price;
        this.userId = userId;
    }
}

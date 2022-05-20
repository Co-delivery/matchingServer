package project.Codelivery.domain.OrderList;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.Codelivery.domain.BaseTimeEntity;
import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table (name = "Order_list")
public class OrderList extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private String listId;

    @Column
    private String item;

    @Column(name = "Order_order_Id")
    private String orderId;

    @Builder
    public OrderList (String item, String orderId) {
        this.item = item;
        this.orderId = orderId;

    }
}

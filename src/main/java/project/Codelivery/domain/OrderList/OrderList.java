package project.Codelivery.domain.OrderList;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table (name = "Order_list")
public class OrderList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private String listId;

    @Column
    private String item;

    @Column(name = "order_id")
    private String orderId;

    @Builder
    public OrderList (String item, String orderId) {
        this.item = item;
        this.orderId = orderId;
    }
}

package project.Codelivery.domain.OrderList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderListRepository extends JpaRepository<OrderList, String> {
    Optional<OrderList> findByOrderId(String orderId);

    List<OrderList> findAllByOrderId(String orderId);
}

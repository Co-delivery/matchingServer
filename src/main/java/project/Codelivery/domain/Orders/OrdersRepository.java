package project.Codelivery.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, String> {
    Optional<Orders> findByUserId(String userId);

    Optional<Orders> deleteByUserId(String userId);
}

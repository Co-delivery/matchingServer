package project.Codelivery.domain.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, String> {
    Optional<Queue> findByUserId(String userId);
}

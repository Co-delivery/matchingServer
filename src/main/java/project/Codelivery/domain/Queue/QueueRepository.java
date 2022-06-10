package project.Codelivery.domain.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, String> {
    Optional<Queue> findByUserId(String userId);
    Optional<Queue> deleteByQueueId(int queueId);
    Queue findByQueueId(int queueId);


    @Query(value = "SELECT restaurant FROM (SELECT restaurant, COUNT(restaurant) AS count FROM Queue WHERE state=0 GROUP BY restaurant)restaurant WHERE restaurant.count > 1", nativeQuery = true)
    List<String> findRestaurant();

    @Query(value = "SELECT location FROM (SELECT location, COUNT(location) AS count FROM Queue WHERE restaurant= :restaurant AND state=0 GROUP BY location)location WHERE location.count > 1", nativeQuery = true)
    List<String> findLocationByRestaurant(@Param("restaurant") String restaurant);

    @Query(value = "SELECT queue_id FROM Queue WHERE restaurant=:restaurant AND location=:location AND state=0 ORDER BY queue_id ASC", nativeQuery = true)
    List<String> findQueueIdByRestaurantAndLocation(@Param("restaurant") String restaurant, @Param("location") String location);

    @Query(value = "SELECT queue_id FROM Queue WHERE timeStamp < DATE_ADD(now(), INTERVAL -5 MINUTE) AND state=0", nativeQuery = true)
    List<String> findQueueIdByTimeStamp();
}

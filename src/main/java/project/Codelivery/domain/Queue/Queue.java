package project.Codelivery.domain.Queue;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.Codelivery.domain.BaseTimeEntity;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table (name = "Queue")
public class Queue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id")
    private int queueId;

    @Column
    private double latitude;
    private double longitude;
    private String restaurant;

    @Column(name = "User_Id")
    private String userId;

    @Builder
    public Queue (double latitude, double longitude, String restaurant, String userId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.restaurant = restaurant;
        this.userId = userId;
    }

}




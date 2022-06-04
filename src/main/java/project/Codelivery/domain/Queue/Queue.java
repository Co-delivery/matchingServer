package project.Codelivery.domain.Queue;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table (name = "Queue")
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id")
    private int queueId;

    @Column
    private String restaurant;
    private int location;
    private int state;
    private double latitude;
    private double longitude;

    @Column(name = "User_Id")
    private String userId;

    @Builder
    public Queue (int location,double latitude, double longitude, String restaurant, String userId) {
        this.location = location;
        this.latitude = latitude;
        this.longitude  = longitude;
        this.restaurant = restaurant;
        this.userId = userId;
    }

}




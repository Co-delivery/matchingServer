package project.Codelivery.domain.Queue;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.Codelivery.domain.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table (name = "Queue")
public class Queue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id")
    private int queueId;

    @Column
    private String restaurant;
    private int location;
    private int state;

    @Column(name = "User_Id")
    private String userId;

    @Builder
    public Queue (int location, String restaurant, String userId) {
        this.location = location;
        this.restaurant = restaurant;
        this.userId = userId;
    }

}




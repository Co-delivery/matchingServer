package project.Codelivery.domain.ChatRoomJoin;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ChatRoomJoin")
public class ChatRoomJoin {

    @Id
    @Column(name = "join_id")
    private int joinId;

    @Column(name = "User_id1")
    private String userId1;

    @Column(name = "User_id2")
    private String userId2;

    @Column(name = "room_id")
    private String roomId;

}

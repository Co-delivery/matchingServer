package project.Codelivery.domain.ChatMessage;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ChatMessage")
public class ChatMessage {

    @Id
    @Column(name = "message_id")
    private int messageId;

    @Column
    private String message;

    @Column(name = "User_id")
    private String userId;

    @Column(name = "room_id")
    private String roomId;

}

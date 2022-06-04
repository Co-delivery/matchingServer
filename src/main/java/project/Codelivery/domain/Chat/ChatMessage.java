package project.Codelivery.domain.Chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ChatMessage")
public class ChatMessage {
    @Id
    @Column(name = "message_id")
    private int messageId;

    @Column(name = "")
    private String userId;
}

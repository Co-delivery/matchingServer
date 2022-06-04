package project.Codelivery.domain.ChatMessage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    ChatMessage findByMessageId(int messageId);
}

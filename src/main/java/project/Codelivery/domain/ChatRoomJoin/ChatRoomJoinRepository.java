package project.Codelivery.domain.ChatRoomJoin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomJoinRepository extends JpaRepository<ChatRoomJoin, String> {
    ChatRoomJoin findByRoomId(String roomId);
}

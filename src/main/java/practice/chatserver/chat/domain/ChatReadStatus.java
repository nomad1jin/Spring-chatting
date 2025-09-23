package practice.chatserver.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_read_statuses")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatReadStatus {

    @Id
    private String id;
    private String messageId;
    private Long memberId;
    private String roomId;
    private boolean isRead;

    public void markAsRead(){
        this.isRead = true;
    }
}

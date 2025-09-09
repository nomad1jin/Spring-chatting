package practice.chatserver.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResDTO {

    @Data
    public static class ChatRoomListResDTO{
        private Long roomId;
        private String roomName;
    }

    @Data
    public static class ChatHistoryListResDTO {
        private String senderName;
        private String message;
    }

    @Data
    public class ChatHistoryResDTO {
        private String senderName;
        private String message;
    }
}

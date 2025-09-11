package practice.chatserver.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.chatserver.chat.domain.ChatMessage;

@Data
public class ChatResDTO {

    @Data
    @AllArgsConstructor
    public static class ChatRoomCardDTO {
        Long roomId;
        String roomName;
        String nickName;
        String message;
        Long unreadCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageResDTO {
        private Long roomId;
        private String senderName;
        private String message;
    }

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
    public static class ChatHistoryResDTO {
        private String senderName;
        private String message;
    }
}

package practice.chatserver.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ChatResDTO {

    @Data
    @AllArgsConstructor
    public static class ChatRoomCreatedDTO {
        String roomId;
        String roomName;
    }

    @Data
    @AllArgsConstructor
    public static class ChatRoomCardDTO {
        String roomId;
        String roomName;
        String userName;
        String message;
        Long unreadCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatMessageResDTO {
        private String messageId;
        private Long senderId;  //프론트는 내 로그인id랑 senderId랑 비교해서 내 메시지, 상대 메시지 구분
        private String senderName;
        private String message;
        private boolean isRead;
    }

    @Data
    public static class ChatRoomListResDTO{
        private String roomId;
        private String roomName;
    }

    @Data
    public static class ChatHistoryListResDTO {
        private String senderName;
        private String message;
    }

}

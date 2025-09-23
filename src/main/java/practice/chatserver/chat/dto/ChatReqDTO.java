package practice.chatserver.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ChatReqDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessageReqDTO {
        private String roomId;
        private Long memberId;
        private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoomCreateDTO {
        private Long targetId;
        private String roomName;
    }

    @Data
    public static class ChatCardReqDTO {
        private String roomId;
    }
}

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
        private Long roomId;
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
        private Long roomId;
    }
}

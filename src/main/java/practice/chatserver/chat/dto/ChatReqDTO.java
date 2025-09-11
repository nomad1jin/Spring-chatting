package practice.chatserver.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatReqDTO {

    @Data
    public static class ChatMessageReqDTO {
        private Long roomId;
        private Long memberId;
        private String message;
    }

    @Data
    public static class ChatRoomCreateDTO {
        private Long targetId;
        private String roomName;
    }

    @Data
    public static class ChatRoomCardDTO {
        private Long roomId;
    }
}

package practice.chatserver.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatReqDTO {

    @Data
    public static class ChatMessageReqDTO {
        private Long roomId;
        private String senderName;
        private String message;
    }
}

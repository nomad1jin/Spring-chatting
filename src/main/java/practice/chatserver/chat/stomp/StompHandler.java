package practice.chatserver.chat.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import practice.chatserver.chat.service.ChatEntryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final ChatEntryService chatEntryService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        if(accessor.getCommand() == StompCommand.CONNECT) {
            chatEntryService.connectSocket(accessor);
        }
        else if(accessor.getCommand() == StompCommand.SUBSCRIBE) {
            chatEntryService.subscribeSocket(accessor);
        }
        return message;
    }
}

package practice.chatserver.chat.domain;

import java.io.Serializable;
import java.util.Objects;

public class ChatParticipationId implements Serializable {
    private Long member;    // 필드명은 엔티티의 @Id 필드명과 같아야 함
    private Long chatRoom;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatParticipationId)) return false;
        ChatParticipationId that = (ChatParticipationId) o;
        return Objects.equals(member, that.member) &&
                Objects.equals(chatRoom, that.chatRoom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, chatRoom);
    }
}

//@Embeddable
//public class ChatParticipationId implements Serializable {
//    private Long memberId;
//    private Long roomId;
//
//    // equals, hashCode 필수
//}
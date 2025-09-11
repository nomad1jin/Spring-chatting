package practice.chatserver.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.chatserver.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String chatMessage;

    //    @Column(nullable = false)
    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private ChatParticipant participant;

    @ManyToOne(fetch = FetchType.LAZY) // 메시지 보낸 회원
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY) // 메시지가 속한 채팅방
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    public void markAsRead(){
        this.isRead = true;
    }

}

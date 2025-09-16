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
public class ChatReadStatus {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "read_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private ChatMessage chatMessage;
    //메세지와 읽음은 일대일이라 생각했는데, 참여자가 둘이므로 메세지 1: 읽음상태 2

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    private boolean isRead;

    public void markAsRead(){
        this.isRead = true;
    }
}

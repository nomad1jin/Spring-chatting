package practice.chatserver.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    private boolean isRead;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="member_id", referencedColumnName="member_id"),
            @JoinColumn(name="room_id", referencedColumnName="room_id")
    })
    private ChatParticipation participation;

    //ChatParticipation에 아무 속성도 없다면 사실상 조인테이블이므로 직접 매핑해도 무방
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "room_id", nullable = false)
//    private ChatRoom chatRoom;
}

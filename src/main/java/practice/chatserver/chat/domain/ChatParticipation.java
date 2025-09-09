package practice.chatserver.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.chatserver.member.entity.Member;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ChatParticipationId.class)
public class ChatParticipation {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    // ChatParticipation 테이블을 사용한다면
    @OneToMany(mappedBy = "participation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();
}


//public class ChatParticipation {
//
//    @EmbeddedId
//    private ChatParticipationId id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("memberId")     // 복합키의 필드명과 매핑
//    @JoinColumn(name = "member_id")
//    private Member member;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("roomId")
//    @JoinColumn(name = "room_id")
//    private ChatRoom chatRoom;
//}

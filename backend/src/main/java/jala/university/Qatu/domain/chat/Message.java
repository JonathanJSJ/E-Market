package jala.university.Qatu.domain.chat;

import jakarta.persistence.*;
import jala.university.Qatu.domain.chat.enums.MessageStatus;
import jala.university.Qatu.domain.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@Table(name = "message")
@Entity(name = "message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    private User sender;

    private String message;

    @Builder.Default
    private MessageStatus messageStatus = MessageStatus.SENT;
}

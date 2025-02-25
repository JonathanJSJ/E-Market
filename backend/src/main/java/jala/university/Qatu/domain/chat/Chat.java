package jala.university.Qatu.domain.chat;

import jakarta.persistence.*;
import jala.university.Qatu.domain.chat.enums.ChatStatus;
import jala.university.Qatu.domain.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuperBuilder
@Table(name = "chat")
@Entity(name = "chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    @ManyToOne
    private User seller;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ChatStatus chatStatus = ChatStatus.OPEN;
}

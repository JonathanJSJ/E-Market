package jala.university.Qatu.repository;

import jala.university.Qatu.domain.chat.Chat;
import jala.university.Qatu.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> findAllByUserOrSeller(User user, User seller);
}

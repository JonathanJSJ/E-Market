package jala.university.Qatu.repository;

import jala.university.Qatu.domain.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}

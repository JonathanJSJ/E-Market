package jala.university.Qatu.domain.chat.dto;

import jala.university.Qatu.domain.chat.enums.ChatStatus;

import java.util.List;

public record ChatDTO (String id, UserChatDTO user, UserChatDTO seller, List<MessageDTO> messages, ChatStatus status) {
}

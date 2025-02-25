package jala.university.Qatu.service;

import jala.university.Qatu.domain.chat.Chat;
import jala.university.Qatu.domain.chat.CreateChatDTO;
import jala.university.Qatu.domain.chat.dto.ChatDTO;
import jala.university.Qatu.domain.chat.Message;
import jala.university.Qatu.domain.chat.dto.MessageDTO;
import jala.university.Qatu.domain.chat.dto.NewMessageDTO;
import jala.university.Qatu.domain.chat.enums.ChatStatus;
import jala.university.Qatu.domain.chat.enums.MessageStatus;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.repository.ChatRepository;
import jala.university.Qatu.repository.MessageRepository;
import jala.university.Qatu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatDTO createChat(CreateChatDTO chatDTO) {
        Chat chat = new Chat();
        User client = findUserById(chatDTO.user());
        chat.setUser(client);
        chat.setSeller(findUserById(chatDTO.seller()));
        chat.setChatStatus(ChatStatus.OPEN);
        chat.setMessages(new ArrayList<>());

        Message firstMessage = new Message();
        firstMessage.setMessageStatus(MessageStatus.SENT);
        firstMessage.setSender(client);
        firstMessage.setMessage(chatDTO.message());

        Message messageSaved = messageRepository.save(firstMessage);

        chat.getMessages().add(messageSaved);

        Chat chatSaved = chatRepository.save(chat);

        messageSaved.setChat(chatSaved);

        messageRepository.save(messageSaved);

        return ConversionService.convertToDto(chat);
    }

    public ChatDTO getChatById(UUID id) {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Chat not found"));
        return ConversionService.convertToDto(chat);
    }

    public List<ChatDTO> getAllChats() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return chatRepository.findAllByUserOrSeller(user, user).stream()
                    .map(ConversionService::convertToDto)
                    .collect(Collectors.toList());
        } else throw new RuntimeException("User not logged");
    }

    public ChatDTO addMessage(UUID chatId, NewMessageDTO messageDTO) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        if (chat.getChatStatus().equals(ChatStatus.CLOSED)) throw new RuntimeException("Chat is closed");

        Message message = new Message();
        message.setChat(chat);
        message.setSender(findUserById(messageDTO.user()));
        message.setMessage(messageDTO.message());

        messageRepository.save(message);

        chat.getMessages().add(message);
        chatRepository.save(chat);

        return ConversionService.convertToDto(chat);
    }

    public ChatDTO updateChatStatus(UUID chatId, String status) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat not found"));
        chat.setChatStatus(ChatStatus.valueOf(status.toUpperCase()));
        chatRepository.save(chat);
        return ConversionService.convertToDto(chat);
    }

    private User findUserById(String userId) {
        return this.userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

package jala.university.Qatu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jala.university.Qatu.domain.chat.CreateChatDTO;
import jala.university.Qatu.domain.chat.dto.ChatDTO;
import jala.university.Qatu.domain.chat.dto.MessageDTO;
import jala.university.Qatu.domain.chat.dto.NewMessageDTO;
import jala.university.Qatu.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Create a new chat with a seller", description = "The chat is create only if the id from the user and the seller exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "User/Seller Id doesn't exist"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping
    public ResponseEntity<ChatDTO> createChat(@RequestBody CreateChatDTO chatDTO) {
        ChatDTO createdChat = chatService.createChat(chatDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChat);
    }

    @Operation(summary = "Get a chat by it's id", description = "Receive a chat information and messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Chat Id doesn't exist"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChatDTO> getChatById(@PathVariable UUID id) {
        ChatDTO chat = chatService.getChatById(id);
        return ResponseEntity.status(HttpStatus.OK).body(chat);
    }

    @Operation(summary = "Get a list of chats", description = "Receive a list of chats information and messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<List<ChatDTO>> getAllChats() {
        List<ChatDTO> chats = chatService.getAllChats();
        return ResponseEntity.status(HttpStatus.OK).body(chats);
    }

    @Operation(summary = "Add a message to the chat", description = "It must post a message on the list of messages on the chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Chat Id doesn't exist"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/{id}/messages")
    public ResponseEntity<ChatDTO> addMessageToChat(@PathVariable UUID id, @RequestBody NewMessageDTO messageDTO) {
        ChatDTO updatedChat = chatService.addMessage(id, messageDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedChat);
    }

    @Operation(summary = "Update the chat status", description = "When the user wants to close the chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Chat Id doesn't exist"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ChatDTO> updateChatStatus(@PathVariable UUID id, @RequestParam String status) {
        ChatDTO updatedChat = chatService.updateChatStatus(id, status);
        return ResponseEntity.status(HttpStatus.OK).body(updatedChat);
    }
}

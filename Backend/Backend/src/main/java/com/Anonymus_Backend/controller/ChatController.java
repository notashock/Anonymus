package com.Anonymus_Backend.controller;

import com.Anonymus_Backend.model.ChatMessageRequest;
import com.Anonymus_Backend.model.ChatSession;
import com.Anonymus_Backend.model.Message;
import com.Anonymus_Backend.model.User;
import com.Anonymus_Backend.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        return chatService.setUserOnline(email);
    }



    @PostMapping("/logout")
    public String logout(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        chatService.setUserOffline(email);
        return "Logged out";
    }


    @PostMapping("/pair")
    public ChatSession pairUsers() {
        ChatSession session = chatService.pairRandomUsers();
        if (session == null) throw new RuntimeException("Not enough users online");
        return session;
    }

    @PostMapping("/messages")
    public Message sendMessage(@RequestBody ChatMessageRequest request) {
        return chatService.sendMessage(
                request.getSessionId(),
                request.getSenderEmail(),
                request.getContent()
        );
    }

    @GetMapping("/messages/{sessionId}")
    public List<Message> getMessages(@PathVariable Long sessionId) {
        return chatService.getMessages(sessionId);

    }
    // Get all online users
    @GetMapping("/online")
    public List<User> getOnlineUsers() {
        System.out.println("done");
        return chatService.getOnlineUsers();
    }

    // Get count of online users
    @GetMapping("/online/count")
    public Map<String, Integer> getOnlineUserCount() {
        int count = chatService.getOnlineUserCount();
        return Map.of("onlineUsers", count);
    }

}


package com.Anonymus_Backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Anonymus_Backend.model.ChatSession;
import com.Anonymus_Backend.model.User;
import com.Anonymus_Backend.service.ChatService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {
        String email = body.get("email");
        if (email != null) {
            chatService.setUserOffline(email);
        }

        try {
            request.logout(); // Ends Spring Security session
        } catch (Exception e) {
            System.err.println("Error during Spring logout: " + e.getMessage());
        }

        // Redirect to Google logout, then back to your frontend
        String googleLogoutUrl = "https://accounts.google.com/Logout?continue=http://localhost:5173/";
        response.sendRedirect(googleLogoutUrl);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/pair")
    public ChatSession pairUsers() {
        ChatSession session = chatService.pairRandomUsers();
        if (session == null) throw new RuntimeException("Not enough users online");
        return session;
    }

//    @PostMapping("/messages")
//    public Message sendMessage(@RequestBody ChatMessageRequest request) {
//        return chatService.sendMessage(
//                request.getSessionId(),
//                request.getSenderEmail(),
//                request.getContent()
//        );
//    }

    @GetMapping("/session/{email}")
    public ChatSession getActiveSession(@PathVariable String email) {
        return chatService.getActiveSessionForUser(email);
    }

//    @GetMapping("/messages/{sessionId}")
//    public List<Message> getMessages(@PathVariable Long sessionId) {
//        return chatService.getMessages(sessionId);
//    }
    // Get all online users
    @GetMapping("/online")
    public List<User> getOnlineUsers() {
        return chatService.getOnlineUsers();
    }


    // Get count of online users
    @GetMapping("/online/count")
    public Map<String, Integer> getOnlineUserCount() {
        int count = chatService.getOnlineUserCount();
        return Map.of("onlineUsers", count);
    }

}


package com.Anonymus_Backend.service;

import com.Anonymus_Backend.model.ChatSession;
import com.Anonymus_Backend.model.Message;
import com.Anonymus_Backend.model.User;
import com.Anonymus_Backend.repository.ChatSessionRepository;
import com.Anonymus_Backend.repository.MessageRepository;
import com.Anonymus_Backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ChatService {
    private final UserRepository userRepo;
    private final ChatSessionRepository sessionRepo;
    private final MessageRepository messageRepo;

    public ChatService(UserRepository userRepo, ChatSessionRepository sessionRepo, MessageRepository messageRepo) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.messageRepo = messageRepo;
    }

    public User setUserOnline(String email) {
        User user = userRepo.findByEmail(email).orElse(User.builder().email(email).build());
        user.setOnline(true);
        return userRepo.save(user);
    }

    public void setUserOffline(String email) {
        userRepo.findByEmail(email).ifPresent(user -> {
            user.setOnline(false);
            userRepo.save(user);
        });
    }

    public ChatSession pairRandomUsers() {
        List<User> onlineUsers = userRepo.findAll().stream().filter(User::isOnline).collect(Collectors.toList());
        if (onlineUsers.size() < 2) return null;
        Collections.shuffle(onlineUsers);
        User user1 = onlineUsers.get(0);
        User user2 = onlineUsers.get(1);

        ChatSession session = ChatSession.builder()
                .user1(user1)
                .user2(user2)
                .active(true)
                .build();
        return sessionRepo.save(session);
    }

    public Message sendMessage(Long sessionId, String senderEmail, String content) {
        ChatSession session = sessionRepo.findById(sessionId).orElseThrow();
        Message message = Message.builder()
                .chatSession(session)
                .senderEmail(senderEmail)
                .content(content)
                .timestamp(System.currentTimeMillis())
                .build();
        return messageRepo.save(message);
    }

    public List<Message> getMessages(Long sessionId) {
        return messageRepo.findByChatSessionIdOrderByTimestampAsc(sessionId);
    }
    public List<User> getOnlineUsers() {
        return userRepo.findByOnlineTrue();
    }

    public int getOnlineUserCount() {
        return userRepo.findByOnlineTrue().size();
    }
}

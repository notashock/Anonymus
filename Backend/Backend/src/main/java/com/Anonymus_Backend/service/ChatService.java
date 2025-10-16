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

    // ✅ Set user online
    public User setUserOnline(String email) {
        User user = userRepo.findByEmail(email)
                .orElse(User.builder().email(email).build());
        user.setOnline(true);
        return userRepo.save(user);
    }

    // ✅ Set user offline and clean up sessions
    public void setUserOffline(String email) {
        userRepo.findByEmail(email).ifPresent(user -> {
            user.setOnline(false);
            userRepo.save(user);

            // Find active sessions for this user
            List<ChatSession> sessions = sessionRepo.findAll().stream()
                    .filter(s -> s.isActive() &&
                            (s.getUser1().getEmail().equals(email) ||
                                    s.getUser2().getEmail().equals(email)))
                    .collect(Collectors.toList());

            for (ChatSession session : sessions) {
                session.setActive(false);
                sessionRepo.save(session);

                // Optional: delete old messages for that session
                messageRepo.deleteAll(
                        messageRepo.findByChatSessionIdOrderByTimestampAsc(session.getId())
                );

                // Keep the other user online & available
                User otherUser = session.getUser1().getEmail().equals(email)
                        ? session.getUser2()
                        : session.getUser1();

                if (otherUser != null) {
                    otherUser.setOnline(true);
                    userRepo.save(otherUser);
                }
            }
        });
    }

    // ✅ Pair only users who are online and not in an active session
    public ChatSession pairRandomUsers() {
        // Get online users who are not already in an active session
        List<User> availableUsers = userRepo.findAll().stream()
                .filter(User::isOnline)
                .filter(user -> !isUserInActiveSession(user))
                .collect(Collectors.toList());

        if (availableUsers.size() < 2) return null;

        Collections.shuffle(availableUsers);
        User user1 = availableUsers.get(0);
        User user2 = availableUsers.get(1);

        ChatSession session = ChatSession.builder()
                .user1(user1)
                .user2(user2)
                .active(true)
                .build();

        return sessionRepo.save(session);
    }

    // ✅ Check if user already has an active session
    private boolean isUserInActiveSession(User user) {
        return sessionRepo.findAll().stream()
                .anyMatch(s -> s.isActive() &&
                        (s.getUser1().getId().equals(user.getId()) ||
                                s.getUser2().getId().equals(user.getId())));
    }

    // ✅ Send message to a session
    public Message sendMessage(Long sessionId, String senderEmail, String content) {
        ChatSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session ID"));

        Message message = Message.builder()
                .chatSession(session)
                .senderEmail(senderEmail)
                .content(content)
                .timestamp(System.currentTimeMillis())
                .build();

        return messageRepo.save(message);
    }

    // ✅ Get messages for a session
    public List<Message> getMessages(Long sessionId) {
        return messageRepo.findByChatSessionIdOrderByTimestampAsc(sessionId);
    }

    // ✅ Get list of all online users
    public List<User> getOnlineUsers() {
        return userRepo.findByOnlineTrue();
    }

    // ✅ Get count of online users
    public int getOnlineUserCount() {
        return userRepo.findByOnlineTrue().size();
    }

    // ✅ Get active session for a specific user
    public ChatSession getActiveSessionForUser(String email) {
        return sessionRepo.findAll().stream()
                .filter(ChatSession::isActive)
                .filter(s -> s.getUser1().getEmail().equals(email) ||
                        s.getUser2().getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
}

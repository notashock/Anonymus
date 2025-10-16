package com.Anonymus_Backend.service;

import com.Anonymus_Backend.model.ChatMessageRequest;
import com.Anonymus_Backend.model.ChatSession;
import com.Anonymus_Backend.model.User;
import com.Anonymus_Backend.repository.ChatSessionRepository;
import com.Anonymus_Backend.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final UserRepository userRepo;
    private final ChatSessionRepository sessionRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(UserRepository userRepo, ChatSessionRepository sessionRepo, SimpMessagingTemplate messagingTemplate) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.messagingTemplate = messagingTemplate;
    }

    // ✅ Set user online
    public User setUserOnline(String email) {
        User user = userRepo.findByEmail(email)
                .orElse(User.builder().email(email).build());
        user.setOnline(true);
        return userRepo.save(user);
    }

    // ✅ Set user offline and notify partner
    public void setUserOffline(String email) {
        userRepo.findByEmail(email).ifPresent(user -> {
            user.setOnline(false);
            userRepo.save(user);

            // Find active sessions for this user
            List<ChatSession> sessions = sessionRepo.findAll().stream()
                    .filter(ChatSession::isActive)
                    .filter(s -> s.getUser1().getEmail().equals(email) || s.getUser2().getEmail().equals(email))
                    .collect(Collectors.toList());

            for (ChatSession session : sessions) {
                session.setActive(false);
                sessionRepo.save(session);

                // Notify the other user if still online
                User otherUser = session.getUser1().getEmail().equals(email) ? session.getUser2() : session.getUser1();
                if (otherUser != null && otherUser.isOnline()) {
                    // Send PARTNER_LEFT system message via WebSocket
                    ChatMessageRequest systemMsg = new ChatMessageRequest();
                    systemMsg.setSessionId(session.getId());
                    systemMsg.setSenderEmail("SYSTEM");
                    systemMsg.setContent("PARTNER_LEFT");
                    messagingTemplate.convertAndSend("/topic/session/" + session.getId(), systemMsg);

                    // Keep the other user online & available
                    otherUser.setOnline(true);
                    userRepo.save(otherUser);
                }
            }
        });
    }

    // ✅ Pair only users who are online and not in an active session
    public ChatSession pairRandomUsers() {
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

    // ✅ Get active session for a specific user
    public ChatSession getActiveSessionForUser(String email) {
        return sessionRepo.findAll().stream()
                .filter(ChatSession::isActive)
                .filter(s -> s.getUser1().getEmail().equals(email) || s.getUser2().getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    // ✅ Get all online users
    public List<User> getOnlineUsers() {
        return userRepo.findByOnlineTrue();
    }

    // ✅ Get online user count
    public int getOnlineUserCount() {
        return userRepo.findByOnlineTrue().size();
    }
}

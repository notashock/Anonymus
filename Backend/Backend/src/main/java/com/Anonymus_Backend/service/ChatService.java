package com.Anonymus_Backend.service;

import com.Anonymus_Backend.model.ChatSession;
import com.Anonymus_Backend.model.ChatMessageRequest;
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

    public ChatService(UserRepository userRepo, ChatSessionRepository sessionRepo,
                       SimpMessagingTemplate messagingTemplate) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.messagingTemplate = messagingTemplate;
    }

    // Set user online
    public User setUserOnline(String email) {
        User user = userRepo.findByEmail(email)
                .orElse(User.builder().email(email).build());
        user.setOnline(true);
        return userRepo.save(user);
    }

    // Set user offline
    public void setUserOffline(String email) {
        userRepo.findByEmail(email).ifPresent(user -> {
            user.setOnline(false);
            userRepo.save(user);

            // Find active sessions for this user
            List<ChatSession> sessions = sessionRepo.findAll().stream()
                    .filter(ChatSession::isActive)
                    .filter(s -> (s.getUser1() != null && s.getUser1().getEmail().equals(email)) ||
                            (s.getUser2() != null && s.getUser2().getEmail().equals(email)))
                    .collect(Collectors.toList());

            for (ChatSession session : sessions) {
                // Remove the user from the session (set to null)
                if (session.getUser1() != null && session.getUser1().getEmail().equals(email)) {
                    session.setUser1(null);
                } else if (session.getUser2() != null && session.getUser2().getEmail().equals(email)) {
                    session.setUser2(null);
                }

                // Delete messages for this user (if using messageRepo)
                // messageRepo.deleteAll(messageRepo.findByChatSessionIdAndSenderEmail(session.getId(), email));

                sessionRepo.save(session);

                // Notify the other user
                User otherUser = (session.getUser1() != null) ? session.getUser1() :
                        (session.getUser2() != null) ? session.getUser2() : null;

                if (otherUser != null && otherUser.isOnline()) {
                    ChatMessageRequest systemMsg = new ChatMessageRequest();
                    systemMsg.setSessionId(session.getId());
                    systemMsg.setSenderEmail("SYSTEM");
                    systemMsg.setContent("PARTNER_LEFT");
                    messagingTemplate.convertAndSend("/topic/session/" + session.getId(), systemMsg);
                }
            }
        });
    }

    // Pair random users
    public ChatSession pairRandomUsers() {
        List<User> availableUsers = userRepo.findAll().stream()
                .filter(User::isOnline)
                .filter(user -> !isUserInActiveSession(user))
                .collect(Collectors.toList());

        if (availableUsers.isEmpty()) return null;

        Collections.shuffle(availableUsers);
        User userToPair = availableUsers.get(0);

        // Try to find a session with an empty slot
        ChatSession sessionWithEmptySlot = sessionRepo.findAll().stream()
                .filter(ChatSession::isActive)
                .filter(s -> s.getUser1() == null || s.getUser2() == null)
                .findFirst()
                .orElse(null);

        if (sessionWithEmptySlot != null) {
            if (sessionWithEmptySlot.getUser1() == null) {
                sessionWithEmptySlot.setUser1(userToPair);
            } else {
                sessionWithEmptySlot.setUser2(userToPair);
            }
            sessionRepo.save(sessionWithEmptySlot);
            return sessionWithEmptySlot;
        }

        // Otherwise, create a new session if there are at least 2 available users
        if (availableUsers.size() < 2) {
            return null;
        }

        User user2 = availableUsers.get(1);
        ChatSession newSession = ChatSession.builder()
                .user1(userToPair)
                .user2(user2)
                .active(true)
                .build();

        return sessionRepo.save(newSession);
    }

    // Check if user already has an active session (null-safe)
    private boolean isUserInActiveSession(User user) {
        return sessionRepo.findAll().stream()
                .anyMatch(s -> s.isActive() &&
                        ((s.getUser1() != null && s.getUser1().getId().equals(user.getId())) ||
                                (s.getUser2() != null && s.getUser2().getId().equals(user.getId()))));
    }

    // Get list of all online users
    public List<User> getOnlineUsers() {
        return userRepo.findByOnlineTrue();
    }

    // Get count of online users
    public int getOnlineUserCount() {
        return userRepo.findByOnlineTrue().size();
    }

    // Get active session for a specific user
    public ChatSession getActiveSessionForUser(String email) {
        return sessionRepo.findAll().stream()
                .filter(ChatSession::isActive)
                .filter(s -> (s.getUser1() != null && s.getUser1().getEmail().equals(email)) ||
                        (s.getUser2() != null && s.getUser2().getEmail().equals(email)))
                .findFirst()
                .orElse(null);
    }
}

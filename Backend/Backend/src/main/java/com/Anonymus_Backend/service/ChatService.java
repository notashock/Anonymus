package com.Anonymus_Backend.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.Anonymus_Backend.model.ChatMessageRequest;
import com.Anonymus_Backend.model.ChatSession;
import com.Anonymus_Backend.model.User;
import com.Anonymus_Backend.repository.ChatSessionRepository;
import com.Anonymus_Backend.repository.UserRepository;

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

            String userId = user.getId();

            List<ChatSession> sessions = sessionRepo.findAll().stream()
                    .filter(ChatSession::isActive)
                    .filter(s -> userId.equals(s.getUser1Id()) || userId.equals(s.getUser2Id()))
                    .collect(Collectors.toList());

            for (ChatSession session : sessions) {
                if (userId.equals(session.getUser1Id())) {
                    session.setUser1Id(null);
                } else if (userId.equals(session.getUser2Id())) {
                    session.setUser2Id(null);
                }

                sessionRepo.save(session);

                // Notify the other user
                String otherUserId = (session.getUser1Id() != null)
                        ? session.getUser1Id()
                        : (session.getUser2Id() != null ? session.getUser2Id() : null);

                if (otherUserId != null) {
                    userRepo.findById(otherUserId).ifPresent(otherUser -> {
                        if (otherUser.isOnline()) {
                            ChatMessageRequest systemMsg = new ChatMessageRequest();
                            systemMsg.setSessionId(session.getId());
                            systemMsg.setSenderEmail("SYSTEM");
                            systemMsg.setContent("PARTNER_LEFT");
                            messagingTemplate.convertAndSend("/topic/session/" + session.getId(), systemMsg);
                        }
                    });
                }
            }
        });
    }

    // Pair random users (with active session checkpoint)
    public ChatSession pairRandomUsers() {
        // Fetch all online users who are not in an active session
        List<User> availableUsers = userRepo.findAll().stream()
                .filter(User::isOnline)
                .collect(Collectors.toList());

        if (availableUsers.isEmpty()) return null;

        Collections.shuffle(availableUsers);
        User userToPair = availableUsers.get(0);

        // ✅ Check if the user is already in an active session
        ChatSession existingSession = sessionRepo.findAll().stream()
                .filter(ChatSession::isActive)
                .filter(s -> {
                    String userId = userToPair.getId();
                    return userId.equals(s.getUser1Id()) || userId.equals(s.getUser2Id());
                })
                .findFirst()
                .orElse(null);

        if (existingSession != null) {
            // Return existing session if already paired
            return existingSession;
        }

        // Find an existing session with an empty slot
        ChatSession sessionWithEmptySlot = sessionRepo.findAll().stream()
                .filter(ChatSession::isActive)
                .filter(s -> s.getUser1Id() == null || s.getUser2Id() == null)
                .findFirst()
                .orElse(null);

        if (sessionWithEmptySlot != null) {
            if (sessionWithEmptySlot.getUser1Id() == null) {
                sessionWithEmptySlot.setUser1Id(userToPair.getId());
            } else {
                sessionWithEmptySlot.setUser2Id(userToPair.getId());
            }
            return sessionRepo.save(sessionWithEmptySlot);
        }

        // Create a new session only if there are at least 2 users available
        List<User> freeUsers = availableUsers.stream()
                .filter(u -> !isUserInActiveSession(u))
                .collect(Collectors.toList());

        if (freeUsers.size() < 2) return null;

        User user2 = freeUsers.get(1);
        ChatSession newSession = ChatSession.builder()
                .user1Id(userToPair.getId())
                .user2Id(user2.getId())
                .active(true)
                .build();

        return sessionRepo.save(newSession);
    }

    // Check if user already has an active session
    private boolean isUserInActiveSession(User user) {
        String userId = user.getId();
        return sessionRepo.findAll().stream()
                .anyMatch(s -> s.isActive() &&
                        (userId.equals(s.getUser1Id()) || userId.equals(s.getUser2Id())));
    }

    // Get list of all online users
    public List<User> getOnlineUsers() {
        return userRepo.findByOnlineTrue();
    }

    // Get count of online users
    public int getOnlineUserCount() {
        return userRepo.findByOnlineTrue().size();
    }

    // Get active session for a specific user email
    public ChatSession getActiveSessionForUser(String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) return null;

        String userId = userOpt.get().getId();

        return sessionRepo.findAll().stream()
                .filter(ChatSession::isActive)
                .filter(s -> userId.equals(s.getUser1Id()) || userId.equals(s.getUser2Id()))
                .findFirst()
                .orElse(null);
    }

    // Get user by email
    public boolean userExists(String email) {
    return userRepo.findByEmail(email).isPresent();
    }

    /**
 * Cleanup session on user logout.
 * - If the other user is online, keep the session.
 * - If the other user is offline or no other user exists, delete the session.
 */
public void handleLogoutCleanup(String email) {
    Optional<User> userOpt = userRepo.findByEmail(email);
    if (userOpt.isEmpty()) return;

    User user = userOpt.get();
    String userId = user.getId();

    // Find all active sessions containing this user
    List<ChatSession> sessions = sessionRepo.findAll().stream()
            .filter(ChatSession::isActive)
            .filter(s -> userId.equals(s.getUser1Id()) || userId.equals(s.getUser2Id()))
            .collect(Collectors.toList());

    for (ChatSession session : sessions) {
        String otherUserId = userId.equals(session.getUser1Id()) ? session.getUser2Id() : session.getUser1Id();

        if (otherUserId != null) {
            Optional<User> otherUserOpt = userRepo.findById(otherUserId);
            if (otherUserOpt.isPresent() && otherUserOpt.get().isOnline()) {
                // Other user is online, just remove this user from session
                if (userId.equals(session.getUser1Id())) {
                    session.setUser1Id(null);
                } else {
                    session.setUser2Id(null);
                }
                sessionRepo.save(session);

                // Notify the other user
                ChatMessageRequest systemMsg = new ChatMessageRequest();
                systemMsg.setSessionId(session.getId());
                systemMsg.setSenderEmail("SYSTEM");
                systemMsg.setContent("PARTNER_LEFT");
                messagingTemplate.convertAndSend("/topic/session/" + session.getId(), systemMsg);

            } else {
                // Other user offline → delete session
                sessionRepo.delete(session);
            }
        } else {
            // No other user → delete session
            sessionRepo.delete(session);
        }
    }
}


}

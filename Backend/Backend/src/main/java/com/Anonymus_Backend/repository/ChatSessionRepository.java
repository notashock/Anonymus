package com.Anonymus_Backend.repository;

import com.Anonymus_Backend.model.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
    // You can add custom query methods if needed
    // e.g., List<ChatSession> findByActiveTrue();
}

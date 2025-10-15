package com.Anonymus_Backend.Backend.repository;

import com.Anonymus_Backend.Backend.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> { }

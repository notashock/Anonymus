package com.Anonymus_Backend.repository;

import com.Anonymus_Backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatSessionIdOrderByTimestampAsc(Long sessionId);
}

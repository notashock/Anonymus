package com.Anonymus_Backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    @Id
    private String id;  // MongoDB uses String IDs

    private String user1Id;  // store User's ID
    private String user2Id;

    private boolean active;
}

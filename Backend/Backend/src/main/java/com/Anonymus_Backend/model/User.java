package com.Anonymus_Backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;  // Stored as ObjectId in Mongo

    @Indexed(unique = true)
    private String email;  // Enforce uniqueness at DB level

    private boolean online;

    private String socketId;
}

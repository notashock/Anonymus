package com.Anonymus_Backend.Backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email; // College email used for login (via OAuth later)

    private boolean online;

    private String socketId; // For real-time connection tracking
}

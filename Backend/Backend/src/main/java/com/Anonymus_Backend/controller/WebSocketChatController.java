package com.Anonymus_Backend.controller;

import com.Anonymus_Backend.model.ChatMessageRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handles incoming chat messages from clients and broadcasts them to the session.
     * This includes normal messages and system messages (like PARTNER_LEFT).
     */
    @MessageMapping("/chat/{sessionId}")
    public void sendMessage(
            @DestinationVariable String sessionId,
            @Payload ChatMessageRequest message
    ) {
        // Forward to all subscribers of the session
        messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
    }
}

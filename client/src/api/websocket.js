// src/api/websocket.js
import SockJS from "sockjs-client";
import Stomp from "stompjs";

let stompClient = null;
let connected = false;

// Use .env variable for backend URL
const BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Connect to WebSocket server and subscribe to a specific chat session.
 * @param {string} sessionId - The chat session ID
 * @param {function} onMessageReceived - Callback for received messages
 * @param {function} onConnected - Callback when connection succeeds
 */
export const connectWebSocket = (sessionId, onMessageReceived, onConnected) => {
  if (connected) return;

  const socket = new SockJS(`${BASE_URL}/ws`);
  stompClient = Stomp.over(socket);

  stompClient.connect({}, () => {
    connected = true;
    console.log("✅ Connected to WebSocket at:", BASE_URL);

    // Subscribe to chat topic
    stompClient.subscribe(`/topic/session/${sessionId}`, (message) => {
      const msgBody = JSON.parse(message.body);
      onMessageReceived(msgBody);
    });

    if (onConnected) onConnected();
  }, (error) => {
    console.error("❌ WebSocket connection error:", error);
  });
};

/**
 * Send message through WebSocket
 * @param {string} sessionId - Chat session ID
 * @param {string} senderEmail - Sender email
 * @param {string} content - Message text
 */
export const sendMessage = (sessionId, senderEmail, content) => {
  if (stompClient && connected) {
    const chatMessage = { sessionId, senderEmail, content };
    stompClient.send(`/app/chat/${sessionId}`, {}, JSON.stringify(chatMessage));
  } else {
    console.warn("⚠️ STOMP client not connected yet");
  }
};

/**
 * Disconnect from WebSocket
 */
export const disconnectWebSocket = () => {
  if (stompClient && connected) {
    stompClient.disconnect(() => {
      connected = false;
      console.log("🔌 Disconnected from WebSocket");
    });
  }
};

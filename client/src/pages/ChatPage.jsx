// src/pages/ChatPage.jsx
import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import {
  connectWebSocket,
  sendMessage,
  disconnectWebSocket,
} from "../api/websocket";
import { logoutUser } from "../api/chatApi";

function ChatPage() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user"));
  const session = JSON.parse(localStorage.getItem("session"));

  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [connected, setConnected] = useState(false);
  const [partnerLeft, setPartnerLeft] = useState(false);

  const messageEndRef = useRef(null);

  // Auto scroll to bottom on new message
  useEffect(() => {
    if (messageEndRef.current) {
      messageEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  // Initialize WebSocket connection
  useEffect(() => {
    if (!user || !session) {
      navigate("/");
      return;
    }

    connectWebSocket(
      session.id,
      (msg) => {
        // Detect system message
        if (msg.senderEmail === "SYSTEM" && msg.content === "PARTNER_LEFT") {
          setPartnerLeft(true);
        } else {
          setMessages((prev) => [...prev, msg]);
        }
      },
      () => setConnected(true)
    );

    return () => {
      disconnectWebSocket();
    };
  }, [session?.id]);

  const handleSend = () => {
    if (!partnerLeft && input.trim()) {
      const newMsg = {
        senderEmail: user.email,
        content: input,
        sessionId: session.id,
      };
      sendMessage(session.id, user.email, input);
      setMessages((prev) => [...prev, newMsg]);
      setInput("");
    }
  };

  const handleLogout = async () => {
    try {
      await logoutUser(user.email);
    } catch (err) {
      console.error("Logout failed:", err);
    } finally {
      localStorage.clear();
      disconnectWebSocket();
      navigate("/");
    }
  };

  const handleFindPartner = () => {
    localStorage.removeItem("session");
    navigate("/pair");
  };

  if (!user || !session) {
    return (
      <div className="h-screen flex items-center justify-center text-gray-600">
        Session expired. Please log in again.
      </div>
    );
  }

  return (
    <div className="flex flex-col h-screen bg-gray-100">
      {/* Header */}
      <div className="flex justify-between items-center bg-indigo-600 text-white px-6 py-4 shadow-md">
        <h2 className="text-xl font-semibold">Anonymous Chat</h2>
        <div className="flex items-center gap-4">
          <p className="text-sm">{user.email}</p>
          <button
            onClick={handleLogout}
            className="bg-red-500 px-3 py-1 rounded-lg hover:bg-red-600 text-sm"
          >
            Logout
          </button>
        </div>
      </div>

      {/* Chat Box */}
      <div className="flex-1 overflow-y-auto px-6 py-4 space-y-3">
        {messages.length === 0 && !partnerLeft && (
          <p className="text-gray-500 text-center mt-10">
            {connected ? "No messages yet..." : "Connecting..."}
          </p>
        )}

        {messages.map((msg, i) => (
          <div
            key={i}
            className={`flex ${
              msg.senderEmail === user.email ? "justify-end" : "justify-start"
            }`}
          >
            <div
              className={`max-w-xs p-3 rounded-2xl text-sm ${
                msg.senderEmail === user.email
                  ? "bg-indigo-600 text-white rounded-br-none"
                  : "bg-gray-300 text-gray-800 rounded-bl-none"
              }`}
            >
              <p>{msg.content}</p>
            </div>
          </div>
        ))}

        {/* Partner left notification */}
        {partnerLeft && (
          <div className="text-center text-red-600 mt-4 font-semibold">
            Your partner has left the chat.
            <button
              onClick={handleFindPartner}
              className="ml-2 bg-indigo-600 text-white px-3 py-1 rounded hover:bg-indigo-700"
            >
              Find Partner
            </button>
          </div>
        )}

        <div ref={messageEndRef} />
      </div>

      {/* Input Box */}
      <div className="flex items-center gap-3 p-4 bg-white border-t">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder={
            partnerLeft ? "Waiting to find a new partner..." : "Type a message..."
          }
          disabled={partnerLeft}
          className="flex-1 border border-gray-300 rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
          onKeyDown={(e) => e.key === "Enter" && handleSend()}
        />
        <button
          onClick={handleSend}
          disabled={partnerLeft}
          className={`px-6 py-2 rounded-full transition ${
            partnerLeft
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-indigo-600 text-white hover:bg-indigo-700"
          }`}
        >
          Send
        </button>
      </div>
    </div>
  );
}

export default ChatPage;

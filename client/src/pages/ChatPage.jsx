// src/pages/ChatPage.jsx
import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { connectWebSocket, sendMessage, disconnectWebSocket } from "../api/websocket";
import { logoutUser } from "../api/chatApi";

const BASE_URL = import.meta.env.VITE_BASE_URL;

function ChatPage() {
  const navigate = useNavigate();
  const [user, setUser] = useState(JSON.parse(localStorage.getItem("user")));
  const [session, setSession] = useState(JSON.parse(localStorage.getItem("session")));

  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [status, setStatus] = useState("connecting"); // connecting | connected | error | partner_left
  const messageEndRef = useRef(null);

  // Scroll to bottom on new messages
  useEffect(() => {
    messageEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // WebSocket lifecycle
  useEffect(() => {
    if (!user || !session) {
      navigate("/");
      return;
    }

    const handlePartnerLeft = async () => {
      setStatus("partner_left");
      alert("Your partner has disconnected. Trying to find a new partner...");
      await findNewPartner();
    };

    connectWebSocket(
      session.id,
      (msg) => {
        if (msg.senderEmail === "SYSTEM" && msg.content === "PARTNER_LEFT") {
          handlePartnerLeft();
        } else {
          // Append all messages from server, including your own
          setMessages((prev) => [...prev, msg]);
        }
      },
      () => setStatus("connected"),
      handlePartnerLeft
    );

    return () => disconnectWebSocket();
  }, [session?.id, user, navigate]);

  // Fetch a new session from backend
  const findNewPartner = async () => {
    try {
      const res = await fetch(`${BASE_URL}/session/${user.email}`);
      if (!res.ok) throw new Error("Failed to fetch session");

      const data = await res.json();
      if (data?.id) {
        // New session found, update local storage and state
        localStorage.setItem("session", JSON.stringify(data));
        setSession(data);
        setMessages([]);
        setStatus("connecting");
        console.log("✅ Connected to new session:", data.id);
      } else {
        alert("No partner available at the moment.");
        localStorage.removeItem("session");
        navigate("/pair"); // redirect to pairing page
      }
    } catch (err) {
      console.error("Error finding new partner:", err);
      alert("Error finding new partner. Try again later.");
    }
  };

  // Send message handler
  const handleSend = () => {
    if (!input.trim() || status !== "connected") return;

    const text = input.trim();
    sendMessage(session.id, user.email, text); // only send to server
    setInput(""); // clear input
  };

  // Logout handler
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

  if (!user || !session) {
    return (
      <div className="h-screen flex items-center justify-center text-gray-600">
        Session expired. Please log in again.
      </div>
    );
  }

  const renderStatusText = () => {
    switch (status) {
      case "connecting":
        return "Connecting to chat...";
      case "error":
        return "Connection failed. Try refreshing.";
      case "partner_left":
        return "Partner disconnected. Looking for a new partner...";
      default:
        return messages.length === 0 ? "No messages yet." : "";
    }
  };

  return (
    <div className="flex flex-col h-screen bg-gray-100">
      {/* Header */}
      <header className="flex justify-between items-center bg-indigo-600 text-white px-6 py-4 shadow-md">
        <h2 className="text-xl font-semibold">Anonymous Chat</h2>
        <div className="flex items-center gap-4">
          <p className="text-sm">{user.email}</p>
          <button
            onClick={handleLogout}
            className="bg-red-500 px-3 py-1 rounded-lg hover:bg-red-600 text-sm transition"
          >
            Logout
          </button>
        </div>
      </header>

      {/* Chat Messages */}
      <main className="flex-1 overflow-y-auto px-6 py-4 space-y-3">
        {messages.length === 0 && (
          <p className="text-gray-500 text-center mt-10 italic">{renderStatusText()}</p>
        )}

        {messages.map((msg, i) => {
          const isUser = msg.senderEmail === user.email;
          return (
            <div key={i} className={`flex ${isUser ? "justify-end" : "justify-start"}`}>
              <div
                className={`max-w-xs p-3 rounded-2xl text-sm shadow-sm ${
                  isUser
                    ? "bg-indigo-600 text-white rounded-br-none"
                    : "bg-gray-200 text-gray-800 rounded-bl-none"
                }`}
              >
                <p>{msg.content}</p>
              </div>
            </div>
          );
        })}

        <div ref={messageEndRef} />
      </main>

      {/* Input Field */}
      <footer className="flex items-center gap-3 p-4 bg-white border-t">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder={
            status === "partner_left"
              ? "Partner disconnected..."
              : status === "error"
              ? "Connection error..."
              : "Type a message..."
          }
          disabled={status !== "connected"}
          onKeyDown={(e) => e.key === "Enter" && handleSend()}
          className="flex-1 border border-gray-300 rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400 disabled:bg-gray-100"
        />
        <button
          onClick={handleSend}
          disabled={status !== "connected"}
          className={`px-6 py-2 rounded-full transition ${
            status !== "connected"
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-indigo-600 text-white hover:bg-indigo-700"
          }`}
        >
          Send
        </button>
      </footer>
    </div>
  );
}

export default ChatPage;

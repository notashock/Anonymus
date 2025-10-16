import React, { useState, useEffect, useRef } from "react";

export default function ChatPage({ user }) {
  const [session, setSession] = useState(null);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [connectionStatus, setConnectionStatus] = useState("Connecting...");
  const messagesEndRef = useRef(null);

  // Scroll to bottom when messages update
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // 🧠 Fetch or create session when user logs in
  useEffect(() => {
    const fetchSession = async () => {
      try {
        console.log("🔄 Checking existing chat session...");

        const existingRes = await fetch(
          `http://localhost:8080/api/chat/session/${user.email}`
        );

        if (existingRes.ok) {
          const sessionData = await existingRes.json();
          if (sessionData && sessionData.id) {
            console.log("✅ Existing session found:", sessionData);
            setSession(sessionData);
            setConnectionStatus("Connected to session " + sessionData.id);
            return;
          }
        }

        console.log("⚙️ No existing session found, pairing now...");

        const pairRes = await fetch("http://localhost:8080/api/chat/pair", {
          method: "POST",
        });

        if (pairRes.ok) {
          const newSession = await pairRes.json();
          console.log("🔗 New session created:", newSession);
          setSession(newSession);
          setConnectionStatus("Connected to session " + newSession.id);
        } else {
          setConnectionStatus("Waiting for another user to join...");
        }
      } catch (err) {
        console.error("❌ Error connecting:", err);
        setConnectionStatus("Connection error");
      }
    };

    fetchSession();
  }, [user]);

  // 📨 Fetch messages every few seconds
  useEffect(() => {
    if (!session) return;0

    const fetchMessages = async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/api/chat/messages/${session.id}`
        );
        if (res.ok) {
          const msgs = await res.json();
          setMessages(msgs);
        }
      } catch (err) {
        console.error("❌ Error fetching messages:", err);
      }
    };

    fetchMessages();
    const interval = setInterval(fetchMessages, 3000); // poll every 3 seconds

    return () => clearInterval(interval);
  }, [session]);

  // ✉️ Send message
  const handleSend = async () => {
    if (!input.trim() || !session) return;

    const messageData = {
      sessionId: session.id,
      senderEmail: user.email,
      content: input,
    };

    try {
      const res = await fetch("http://localhost:8080/api/chat/messages", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(messageData),
      });

      if (res.ok) {
        const newMsg = await res.json();
        setMessages((prev) => [...prev, newMsg]);
        setInput("");
        console.log("📤 Message sent:", newMsg);
      } else {
        console.error("❌ Failed to send message:", await res.text());
      }
    } catch (err) {
      console.error("❌ Error sending message:", err);
    }
  };

  // 🟢 Connection log heartbeat
  useEffect(() => {
    const interval = setInterval(() => {
      console.log("🟢 Connection status:", connectionStatus);
    }, 5000);
    return () => clearInterval(interval);
  }, [connectionStatus]);

  return (
    <div className="flex flex-col h-screen bg-gray-100">
      {/* Header */}
      <div className="bg-indigo-600 text-white p-4 flex justify-between items-center">
        <h2 className="text-xl font-semibold">Campus Chat</h2>
        <span>{user.name}</span>
      </div>

      {/* Connection status */}
      <div className="text-center text-sm text-gray-600 py-1 bg-gray-200">
        {connectionStatus}
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-3">
        {messages.map((msg, index) => (
          <div
            key={index}
            className={`p-3 rounded-lg max-w-xs ${
              msg.senderEmail === user.email
                ? "bg-indigo-100 self-end ml-auto"
                : "bg-white self-start"
            }`}
          >
            <span className="text-sm font-semibold">
              {msg.senderEmail === user.email ? "You" : msg.senderEmail}
            </span>
            <p className="text-gray-800 break-words">{msg.content}</p>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* Input */}
      <div className="p-4 bg-white flex gap-2">
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSend()}
          type="text"
          placeholder="Type a message..."
          className="flex-1 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
        />
        <button
          onClick={handleSend}
          className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
        >
          Send
        </button>
      </div>
    </div>
  );
}

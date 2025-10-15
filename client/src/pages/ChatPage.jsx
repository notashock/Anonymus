import React, { useState, useEffect, useRef } from "react";

export default function ChatPage({ user }) {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const messagesEndRef = useRef(null);

  // Scroll to bottom when messages update
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = () => {
    if (!input.trim()) return;

    // For now, mock sending message
    setMessages(prev => [...prev, { sender: user.name, text: input }]);
    setInput("");
  };

  return (
    <div className="flex flex-col h-screen bg-gray-100">
      {/* Header */}
      <div className="bg-indigo-600 text-white p-4 flex justify-between items-center">
        <h2 className="text-xl font-semibold">Campus Chat</h2>
        <span>{user.name}</span>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-3">
        {messages.map((msg, index) => (
          <div
            key={index}
            className={`p-3 rounded-lg max-w-xs ${
              msg.sender === user.name ? "bg-indigo-100 self-end" : "bg-white self-start"
            }`}
          >
            <span className="text-sm font-semibold">{msg.sender}</span>
            <p className="text-gray-800">{msg.text}</p>
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

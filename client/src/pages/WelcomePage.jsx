import React, { useEffect, useState } from "react";

export default function WelcomePage({ setUser }) {
  const [onlineCount, setOnlineCount] = useState(0);

  useEffect(() => {
    const fetchOnlineCount = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/chat/online/count");
        if (!res.ok) return;
        const data = await res.json();
        setOnlineCount(data.onlineUsers || 0);
      } catch (err) {
        console.error("Failed to fetch online users", err);
      }
    };
    fetchOnlineCount();
  }, []);

  const handleLoginClick = () => {
    // This will switch to LoginPage in App.jsx
    setUser(null);
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-b from-indigo-100 to-white px-4">
      <h1 className="text-4xl font-bold text-indigo-700 mb-6">
        Welcome to Campus Chat
      </h1>
      <p className="text-gray-600 mb-4 text-center max-w-md">
        Connect anonymously with fellow students in your college.
      </p>
      <p className="text-gray-500 mb-10">
        Currently online: {onlineCount} user{onlineCount !== 1 ? "s" : ""}
      </p>
      <button
        onClick={handleLoginClick}
        className="px-6 py-3 bg-indigo-600 text-white rounded-xl shadow hover:bg-indigo-700 transition-all"
      >
        Login to Continue
      </button>
    </div>
  );
}

import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import {
  pairUsers,
  getOnlineUserCount,
  getActiveSession,
} from "../api/chatApi";

function PairPage() {
  const [loading, setLoading] = useState(false);
  const [onlineCount, setOnlineCount] = useState(0);
  const [waitingMessage, setWaitingMessage] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

  // Handle email from redirect or fallback to localStorage
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const emailFromUrl = params.get("email");
    const existingUser = JSON.parse(localStorage.getItem("user"));

    if (emailFromUrl && (!existingUser || existingUser.email !== emailFromUrl)) {
      localStorage.setItem("user", JSON.stringify({ email: emailFromUrl }));
    } else if (!emailFromUrl && !existingUser) {
      navigate("/"); // not logged in
    }
  }, [location.search, navigate]);

  const user = JSON.parse(localStorage.getItem("user"));

  // Check active session on mount
  useEffect(() => {
    if (!user) return;

    const checkActiveSession = async () => {
      try {
        const session = await getActiveSession(user.email);
        if (session) {
          localStorage.setItem("session", JSON.stringify(session));
          navigate("/chat");
        }
      } catch (err) {
        console.warn("No active session:", err);
      }
    };

    checkActiveSession();
  }, [user, navigate]);

  // Poll online user count
  useEffect(() => {
    const fetchOnline = async () => {
      try {
        const res = await getOnlineUserCount();
        const count = res.onlineUsers || 0;
        setOnlineCount(count);
        setWaitingMessage(count < 2 ? "Waiting for more users to come online..." : "");
      } catch (err) {
        console.error("Error fetching online count:", err);
      }
    };

    fetchOnline();
    const interval = setInterval(fetchOnline, 3000);
    return () => clearInterval(interval);
  }, []);

  const handlePair = async () => {
    setLoading(true);
    try {
      const session = await pairUsers();
      if (!session) {
        alert("Not enough users online. Please wait.");
        setLoading(false);
        return;
      }
      localStorage.setItem("session", JSON.stringify(session));
      navigate("/chat");
    } catch (err) {
      console.error("Pairing failed:", err);
      alert("Pairing failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

const handleLogout = async () => {
  const user = JSON.parse(localStorage.getItem("user"));
  try {
    await fetch("http://localhost:8080/logout", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: user?.email }),
      credentials: "include",
    });
  } catch (err) {
    console.error("Error logging out:", err);
  } finally {
    localStorage.clear();
    // let the backend redirect handle navigation
    window.location.href = "http://localhost:8080/logout";
  }
};


  // If user manually hits /pair after already paired, redirect them
  useEffect(() => {
    const session = localStorage.getItem("session");
    if (session) navigate("/chat");
  }, [navigate]);

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-50">
      <div className="bg-white shadow-md rounded-2xl p-8 text-center w-96">
        <h2 className="text-2xl font-semibold mb-4 text-gray-800">
          Welcome, {user?.email}
        </h2>
        <p className="text-gray-500 mb-4">
          Online users: <strong>{onlineCount}</strong>
        </p>
        {waitingMessage && (
          <p className="text-sm text-red-500 mb-3">{waitingMessage}</p>
        )}
        <button
          onClick={handlePair}
          disabled={loading || onlineCount < 2}
          className={`w-full py-3 rounded-lg transition ${
            loading || onlineCount < 2
              ? "bg-gray-300 text-gray-500 cursor-not-allowed"
              : "bg-indigo-600 text-white hover:bg-indigo-700"
          }`}
        >
          {loading ? "Pairing..." : "Find a Partner 🔗"}
        </button>

        <button
          onClick={handleLogout}
          className="w-full bg-gray-200 text-gray-700 py-2 rounded-lg mt-4 hover:bg-gray-300 transition"
        >
          Logout
        </button>
      </div>
    </div>
  );
}

export default PairPage;

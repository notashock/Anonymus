import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { pairUsers, getOnlineUserCount, getCurrentUser } from "../api/chatApi";

function PairPage() {
  const [loading, setLoading] = useState(false);
  const [onlineCount, setOnlineCount] = useState(0);
  const [waitingMessage, setWaitingMessage] = useState("");
  const [user, setUser] = useState(null);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const API_BASE = import.meta.env.VITE_BASE_URL;

  // ✅ Check email param from redirect
  useEffect(() => {
    const emailParam = searchParams.get("email");

    const verifyUser = async (email) => {
      try {
        // Check if user exists in DB
        const res = await fetch(`${API_BASE}/api/chat/userExists?email=${email}`, {
          method: "GET",
          credentials: "include",
        });
        const exists = await res.json();

        if (exists?.exists) {
          // ✅ Save user info
          const userObj = { email };
          setUser(userObj);
          localStorage.setItem("user", JSON.stringify(userObj));
        } else {
          console.warn("User not found in DB, redirecting to login...");
          navigate("/"); // Redirect to login if not found
        }
      } catch (err) {
        console.error("Error verifying user:", err);
        navigate("/");
      }
    };

    // If email param present from redirect, verify it
    if (emailParam) {
      verifyUser(emailParam);
    } else {
      // fallback: check backend current user (in case of refresh)
      (async () => {
        try {
          const currentUser = await getCurrentUser();
          if (currentUser?.email) {
            setUser(currentUser);
            localStorage.setItem("user", JSON.stringify(currentUser));
          } else {
            navigate("/");
          }
        } catch (err) {
          console.error("Error fetching current user:", err);
          navigate("/");
        }
      })();
    }
  }, [searchParams, navigate, API_BASE]);

  // ✅ Poll online user count
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

  // ✅ Pair user (backend now handles session reuse)
  const handlePair = async () => {
    if (!user) return;
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

  // ✅ Logout handler (includes backend logout + google revoke)
  const handleLogout = async () => {
    try {
      await fetch(`${API_BASE}/api/chat/logout`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: user?.email }),
        credentials: "include",
      });
    } catch (err) {
      console.error("Error logging out:", err);
    } finally {
      localStorage.clear();
      window.location.href = `${API_BASE}/api/chat/logout`;
    }
  };

  // ✅ Auto-redirect if session already exists
  useEffect(() => {
    const session = localStorage.getItem("session");
    if (session) navigate("/chat");
  }, [navigate]);

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-50">
      <div className="bg-white shadow-md rounded-2xl p-8 text-center w-96">
        <h2 className="text-2xl font-semibold mb-4 text-gray-800">
          {user ? `Welcome, ${user.email}` : "Loading user..."}
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

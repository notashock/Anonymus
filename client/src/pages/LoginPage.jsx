import React, { useState } from "react";

export default function LoginPage({ setUser }) {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async () => {
    if (!email.trim()) {
      setError("Please enter your college email");
      return;
    }

    try {
      const res = await fetch("http://localhost:8080/api/chat/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
      });

      if (!res.ok) throw new Error("Login failed");

      const userData = await res.json();
      setUser(userData);
    } catch (err) {
      console.error(err);
      setError("Failed to login. Check your email or server.");
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-b from-indigo-100 to-white px-4">
      <div className="bg-white shadow-lg rounded-xl p-8 max-w-md w-full text-center">
        <h1 className="text-3xl font-bold text-indigo-700 mb-4">Sign In</h1>
        <input
          type="email"
          placeholder="Your college email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full mb-4 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
        />
        <button
          onClick={handleLogin}
          className="w-full py-3 bg-indigo-600 text-white rounded-lg shadow hover:bg-indigo-700 transition-all"
        >
          Login
        </button>
        {error && <p className="text-red-500 mt-2">{error}</p>}
      </div>
    </div>
  );
}

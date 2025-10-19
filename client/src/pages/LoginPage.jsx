// src/pages/LoginPage.jsx
import React, { useState } from "react";

function LoginPage() {
  const [loading, setLoading] = useState(false);

  const handleGoogleLogin = () => {
    setLoading(true);
    const baseUrl = import.meta.env.VITE_BASE_URL;
    window.location.href = `${baseUrl}/oauth2/authorization/google?prompt=select_account`;
  };

  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-lg w-96 text-center">
        <h1 className="text-2xl font-bold mb-6 text-gray-800">
          Anonymous Chat Login
        </h1>

        <button
          onClick={handleGoogleLogin}
          disabled={loading}
          className="w-full flex items-center justify-center gap-3 bg-red-500 text-white py-3 rounded-lg hover:bg-red-600 transition disabled:opacity-70"
        >
          <img
            src="../assets/google.png"
            alt="Google Logo"
            className="w-5 h-5"
          />
          {loading ? "Redirecting..." : "Continue with Google"}
        </button>

        <p className="text-gray-500 text-sm mt-4">
          Only college accounts are allowed
        </p>
      </div>
    </div>
  );
}

export default LoginPage;

// src/api/chatApi.js
import axios from "axios";

// Use .env variable for base URL
const BASE_URL = import.meta.env.VITE_API_BASE_URL;

const api = axios.create({
  baseURL: `${BASE_URL}/api/chat`,
});

// ✅ Login a user (set user online)
export const loginUser = async (email) => {
  try {
    const res = await api.post("/login", { email });
    return res.data;
  } catch (err) {
    console.error("Login failed:", err);
    throw err;
  }
};

// ✅ Logout user
export const logoutUser = async (email) => {
  try {
    const res = await api.post("/logout", { email });
    return res.data;
  } catch (err) {
    console.error("Logout failed:", err);
    throw err;
  }
};

// ✅ Pair random users
export const pairUsers = async () => {
  try {
    const res = await api.post("/pair");
    return res.data;
  } catch (err) {
    console.error("Pairing failed:", err);
    throw err;
  }
};

// ✅ Get active session for a user
export const getActiveSession = async (email) => {
  try {
    const res = await api.get(`/session/${email}`);
    return res.data;
  } catch (err) {
    console.error("Get active session failed:", err);
    throw err;
  }
};

// ✅ Get all online users
export const getOnlineUsers = async () => {
  try {
    const res = await api.get("/online");
    return res.data;
  } catch (err) {
    console.error("Fetching online users failed:", err);
    throw err;
  }
};

// ✅ Get online user count
export const getOnlineUserCount = async () => {
  try {
    const res = await api.get("/online/count");
    return res.data;
  } catch (err) {
    console.error("Fetching online user count failed:", err);
    throw err;
  }
};

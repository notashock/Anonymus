# 🕵️‍♂️ Anonymus - Anonymous Chat Portal

A secure and anonymous chat platform built using **Spring Boot**, **React.js**, and **MongoDB**, allowing users to communicate without revealing their identity.  
The system ensures privacy, message encryption, and seamless real-time chatting through WebSockets.

---

<details>
<summary>🚀 Project Overview</summary>

The **Anonymus Chat Portal** is designed for users who want to communicate without revealing their personal identity.  
It allows users to join chat sessions, send and receive messages anonymously, and experience a safe communication environment.

This project combines the power of:
- **Spring Boot (Backend)** for REST API, WebSocket, and MongoDB integration.  
- **React.js (Frontend)** for responsive, real-time UI.  
- **MongoDB** for secure message and user data storage.  

</details>

---

<details>
<summary>✨ Features</summary>

✅ User registration and login with email verification (OTP-based).  
✅ Anonymous chat sessions (no usernames displayed in chat).  
✅ Secure message storage using MongoDB.  
✅ WebSocket integration for real-time messaging.  
✅ Environment variable support using `.env` file.  
✅ RESTful API endpoints for chat, users, and sessions.  
✅ CORS enabled for frontend-backend communication.  

</details>

---

<details>
<summary>⚙️ Tech Stack</summary>

**Frontend:** React.js, HTML, CSS, JavaScript  
**Backend:** Spring Boot, Java 17, WebSocket  
**Database:** MongoDB  
**Authentication:** OTP-based Email verification  
**Environment Management:** Dotenv  
**Build Tool:** Maven  

</details>

---

## 📁 Project Structure

### 🖥️ Frontend (/Frontend)

```bash
src/
├── components/      # React UI Components
├── pages/           # Different pages (Home, Chat, Login, etc.)
├── services/        # API service calls
├── App.js           # Main routing logic
└── package.json     # Dependencies and scripts


---

src/main/java/com/Anonymus_Backend/
├── controller/      # REST Controllers
├── model/           # Entity classes
├── service/         # Business logic
├── repository/      # MongoDB repositories
├── config/          # Configuration (CORS, EnvConfig, WebSocket)
└── BackendApplication.java  # Spring Boot main file

resources/
├── application.properties
├── static/
└── templates/
Anonymus/
├── Backend/
├── Frontend/
└── README.md


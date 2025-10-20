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

<details>
<summary>📁 Project Structure</summary>

### 🗂️ Root Folder

```bash
Anonymus/
├── Backend/
├── Frontend/
└── README.md
Backend
Backend/
├── src/
│   ├── main/
│   │   ├── java/com/Anonymus_Backend/
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── model/           # Entity classes
│   │   │   ├── service/         # Business logic
│   │   │   ├── repository/      # MongoDB Repositories
│   │   │   ├── config/          # Config files (CORS, EnvConfig, WebSocket)
│   │   │   └── BackendApplication.java  # Spring Boot main file
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
└── pom.xml

Frontend
Frontend/
├── src/
│   ├── components/   # React components
│   ├── pages/        # UI pages (Login, Chat, Home)
│   ├── services/     # API services
│   ├── App.js        # Main routing logic
│   └── index.js      # Entry point
├── public/
│   ├── index.html
│   └── favicon.ico
└── package.json
```
</details>

---

<details>
  <summary>🛠️ Setup Instructions</summary>

  ### Clone the repository
  ```bash
  git clone https://github.com/your-username/Anonymus.git
  cd Anonymus
```
  ### Setup Backend
  ```bash
  cd Backend
# Add your environment variables in .env
# Example:
# MONGODB_URI=your_mongodb_connection_string
# GOOGLE_CLIENT_ID=your_google_client_id
# GOOGLE_CLIENT_SECRET=your_google_client_secret

mvn spring-boot:run
```
 ### Setup Frontend
 ```bash
cd Frontend
npm install
npm start
```
### Access the App

Frontend: http://localhost:3000

Backend: http://localhost:8080

</details>

---
<details> <summary>📧 Contact</summary>

Authors: Pavan Yegireddy,Ashok Bavireddy
Email: [pavany3712@gmail.com,
ashuashok@gmail.com
]
GitHub: github.com/yegireddypavan

</details>

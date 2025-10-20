🕵️‍♂️ Anonymus Chat Application

A real-time anonymous chat platform built using Spring Boot, React, and MongoDB.
It allows users to create sessions, join chat rooms, and exchange messages securely — all while staying anonymous.

🚀 Features

💬 Anonymous Chat Sessions — users can chat without revealing their identity.

⚡ Real-time Messaging — powered by WebSocket for instant communication.

🔐 Google Authentication (OAuth 2.0) — secure login using Gmail.

🗄️ MongoDB Atlas Integration — cloud-based storage for users, sessions, and messages.

📦 Environment-based Configuration using .env for sensitive credentials.

🌐 RESTful API for all backend operations.

🧩 Modular Architecture — clean separation between controller, service, and repository layers.

🏗️ Tech Stack
Layer	Technology
Frontend	React.js, HTML, CSS, Axios
Backend	Spring Boot (Java 17), WebSocket, REST API
Database	MongoDB Atlas
Authentication	Google OAuth 2.0
Environment Management	Dotenv Java
Build Tools	Maven
Version Control	Git & GitHub

📁 Project Structure
Anonymus/
│
├── Backend/
│   ├── src/main/java/com/Anonymus_Backend/
│   │   ├── controller/       # API endpoints
│   │   ├── service/          # Business logic
│   │   ├── model/            # Data models
│   │   ├── repository/       # MongoDB repositories
│   │   └── BackendApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── .env              # Environment variables
│   └── pom.xml
│
├── Frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── README.md
│
└── README.md                 # You're here 🚀
⚙️ Environment Setup
1️⃣ Create a .env file (inside Backend/ or src/main/resources)
MONGODB_URI=mongodb+srv://<username>:<password>@cluster.mongodb.net/?retryWrites=true&w=majority
MONGODB_DATABASE=anonymus_chat
GOOGLE_CLIENT_ID=<your-google-client-id>
GOOGLE_CLIENT_SECRET=<your-google-client-secret>



▶️ How to Run
🖥️ Backend (Spring Boot)
cd Backend
mvn clean install
mvn spring-boot:run



🌐 Frontend (React)
cd Frontend
npm install
npm start

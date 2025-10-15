import React, { useState } from "react";
import LoginPage from "./pages/LoginPage";
import WelcomePage from "./pages/WelcomePage";
import ChatPage from "./pages/ChatPage";

function App() {
  const [user, setUser] = useState("user1");
  const [showLogin, setShowLogin] = useState(false);

  if (!user) {
    return showLogin ? (
      <LoginPage setUser={setUser} />
    ) : (
      <WelcomePage setUser={() => setShowLogin(true)} />
    );
  }

  return <ChatPage user={user} />;
}

export default App;

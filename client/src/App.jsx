import { Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import PairPage from "./pages/PairPage";
import ChatPage from "./pages/ChatPage";
import WelcomePage from "./pages/WelcomePage";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/pair" element={<PairPage />} />
      <Route
        path="/chat"
        element={
          <ProtectedRoute>
            <ChatPage />
          </ProtectedRoute>
        }
      />
      {/* <Route
        path="/welcome"
        element={
          <ProtectedRoute>
            <WelcomePage />
          </ProtectedRoute>
        }
      /> */}
    </Routes>
  );
}

export default App;

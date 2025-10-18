package com.Anonymus_Backend.config;

import com.Anonymus_Backend.service.ChatService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final ChatService chatService;

    public CustomOAuth2SuccessHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        if (email != null) {
            chatService.setUserOnline(email);
            // Encode email for safe URL passing
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            response.sendRedirect("http://localhost:5173/pair?email=" + encodedEmail);
        } else {
            // fallback redirect if no email found
            response.sendRedirect("http://localhost:5173/");
        }
    }
}

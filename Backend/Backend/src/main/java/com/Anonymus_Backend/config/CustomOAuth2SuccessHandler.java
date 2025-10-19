package com.Anonymus_Backend.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.Anonymus_Backend.service.ChatService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final ChatService chatService;

    @Value("${FRONTEND_URL}") // inject frontend URL from environment
    private String frontendUrl;

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
            // Set user online in backend
            chatService.setUserOnline(email);

            // Redirect frontend to pair page using environment variable
            response.sendRedirect(frontendUrl + "/pair");
        } else {
            // fallback redirect if no email found
            response.sendRedirect(frontendUrl + "/");
        }
    }
}

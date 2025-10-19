package com.Anonymus_Backend.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    @Value("${FRONTEND_URL}")
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
            // Mark user online
            chatService.setUserOnline(email);

            // URL-encode the email to avoid special character issues
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

            // Redirect to frontend with email as query param
            String redirectUrl = String.format("%s/pair?email=%s", frontendUrl, encodedEmail);
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(frontendUrl + "/");
        }
    }
}

package com.Anonymus_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {

        // ✅ Load environment variables from .env (only locally)
        Dotenv dotenv = null;
        try {
            dotenv = Dotenv.load(); // loads .env automatically from project root
        } catch (Exception e) {
            System.out.println("⚠️ No .env file found. Using system environment variables.");
        }

        // Fetch variables (priority: System > .env)
        String mongoUri = System.getenv("MONGODB_URI");
        if (mongoUri == null && dotenv != null) mongoUri = dotenv.get("MONGODB_URI");

        String mongoDatabase = System.getenv("MONGODB_DATABASE");
        if (mongoDatabase == null && dotenv != null) mongoDatabase = dotenv.get("MONGODB_DATABASE");

        String googleClientId = System.getenv("GOOGLE_CLIENT_ID");
        if (googleClientId == null && dotenv != null) googleClientId = dotenv.get("GOOGLE_CLIENT_ID");

        String googleClientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
        if (googleClientSecret == null && dotenv != null) googleClientSecret = dotenv.get("GOOGLE_CLIENT_SECRET");

        String frontendUrl = System.getenv("FRONTEND_URL");
        if (frontendUrl == null && dotenv != null) frontendUrl = dotenv.get("FRONTEND_URL");

        // ✅ Set them as system properties (for Spring to read)
        if (mongoUri != null) System.setProperty("MONGODB_URI", mongoUri);
        if (mongoDatabase != null) System.setProperty("MONGODB_DATABASE", mongoDatabase);
        if (googleClientId != null) System.setProperty("GOOGLE_CLIENT_ID", googleClientId);
        if (googleClientSecret != null) System.setProperty("GOOGLE_CLIENT_SECRET", googleClientSecret);
        if (frontendUrl != null) System.setProperty("FRONTEND_URL", frontendUrl);

        SpringApplication.run(BackendApplication.class, args);
    }
}

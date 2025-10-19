package com.Anonymus_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {

        // Read environment variables directly
        String mongoUri = System.getenv("MONGODB_URI");
        String mongoDatabase = System.getenv("MONGODB_DATABASE");
        String googleClientId = System.getenv("GOOGLE_CLIENT_ID");
        String googleClientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
        String frontendUrl = System.getenv("FRONTEND_URL");

        // Set system properties (if your code depends on them)
        if (mongoUri != null) System.setProperty("MONGODB_URI", mongoUri);
        if (mongoDatabase != null) System.setProperty("MONGODB_DATABASE", mongoDatabase);
        if (googleClientId != null) System.setProperty("GOOGLE_CLIENT_ID", googleClientId);
        if (googleClientSecret != null) System.setProperty("GOOGLE_CLIENT_SECRET", googleClientSecret);
        if (frontendUrl != null) System.setProperty("FRONTEND_URL", frontendUrl);

        SpringApplication.run(BackendApplication.class, args);
    }

}

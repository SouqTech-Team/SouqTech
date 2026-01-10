package org.stand.springbootecommerce.utiil;

import jakarta.servlet.http.HttpServletRequest;

public class JwtUtils {
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    private JwtUtils() {
        // Utility class
    }

    public static String getJwtFromRequest(HttpServletRequest request) {
        // Extract the auth header from request
        String authHeader = request.getHeader(HEADER_STRING);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            // Extract the token from auth header
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}

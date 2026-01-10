package org.stand.springbootecommerce.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.stand.springbootecommerce.service.JwtService;
import org.stand.springbootecommerce.utiil.JwtUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String jwtToken;

        // Extract JWT token from request's header
        jwtToken = JwtUtils.getJwtFromRequest(request);
        if (jwtToken == null) {
            filterChain.doFilter(request, response); // pass request and response to the next filter
            return; // stop execution of that filter
        }

        // Extract user username from JWT
        String userUsername = null;
        try {
            userUsername = jwtService.extractUsername(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userUsername != null && SecurityContextHolder.getContext().getAuthentication() == null) { // if user is
                                                                                                      // already
                                                                                                      // authenticated,
                                                                                                      // I don't need to
                                                                                                      // perform again
                                                                                                      // all checks and
                                                                                                      // setting (like
                                                                                                      // update security
                                                                                                      // context)
            // Get user from db
            UserDetails userDetails = null;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(userUsername);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (userDetails != null && jwtService.isTokenValid(jwtToken, userDetails)) {
                log.error("In 2nd if");

                // Update security context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
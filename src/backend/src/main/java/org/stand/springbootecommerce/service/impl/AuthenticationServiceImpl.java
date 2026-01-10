package org.stand.springbootecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.stand.springbootecommerce.dto.request.AuthenticationRequest;
import org.stand.springbootecommerce.dto.request.RegisterRequest;
import org.stand.springbootecommerce.dto.response.AuthenticationResponse;
import org.stand.springbootecommerce.dto.response.BaseResponseBody;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.error.BaseException;
import org.stand.springbootecommerce.error.UserEmailAlreadyTakenException;
import org.stand.springbootecommerce.error.UserNotAuthenticatedException;
import org.stand.springbootecommerce.error.UserNotFoundException;
import org.stand.springbootecommerce.repository.UserRepository;
import org.stand.springbootecommerce.service.AuthenticationService;
import org.stand.springbootecommerce.service.JwtService;

import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MessageSource messageSource;

    public BaseResponseBody register(RegisterRequest request) throws BaseException {
        // Check repository persistence constraints
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserEmailAlreadyTakenException();
        }
        // Save user to repository
        User user = User.builder()
                .name(request.getName().trim().toLowerCase())
                .surname(request.getSurname().trim().toLowerCase())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(Objects.requireNonNull(user));
        // Create and return success response
        Locale locale = Locale.getDefault();
        return new BaseResponseBody(
                messageSource.getMessage("user.register.success", null, Objects.requireNonNull(locale)));
    }

    @Override
    public User me() throws UserNotFoundException, UserNotAuthenticatedException {
        // Get the current authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UserNotAuthenticatedException();
        }
        // Get authenticated user email
        String email = authentication.getName();
        // Return user entity by email
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request)
            throws BadCredentialsException, BaseException {
        // Authenticate the user with the provided credentials
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(null);
        }
        // (user authentication successful at this point)

        // Get user from repository
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                UserNotFoundException::new);

        // Generate JWT token
        String jwt = jwtService.generateToken(user);

        // Create and return success response
        return new AuthenticationResponse(jwt);
    }

}

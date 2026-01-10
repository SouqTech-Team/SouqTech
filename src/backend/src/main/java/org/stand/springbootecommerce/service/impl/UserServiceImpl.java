package org.stand.springbootecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.stand.springbootecommerce.dto.request.UserUpdateRequest;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.error.UserNotAuthenticatedException;
import org.stand.springbootecommerce.error.UserNotFoundException;
import org.stand.springbootecommerce.repository.UserRepository;
import org.stand.springbootecommerce.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User updateUser(UserUpdateRequest userUpdateRequest)
            throws UserNotFoundException, UserNotAuthenticatedException {
        // Check user authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UserNotAuthenticatedException();
        }
        // Get authenticated user
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        // Update and Save user
        user.setName(userUpdateRequest.getName());
        user.setSurname(userUpdateRequest.getSurname());
        userRepository.save(user);
        // Return new user
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }
}

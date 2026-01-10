package org.stand.springbootecommerce.service;

import org.stand.springbootecommerce.dto.request.UserUpdateRequest;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.error.UserNotAuthenticatedException;
import org.stand.springbootecommerce.error.UserNotFoundException;

public interface UserService {
    User updateUser(UserUpdateRequest userUpdateRequest) throws UserNotFoundException, UserNotAuthenticatedException;

    User getUserByEmail(String email);
}
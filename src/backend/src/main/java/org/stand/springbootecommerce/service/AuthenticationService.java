package org.stand.springbootecommerce.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.stand.springbootecommerce.dto.request.AuthenticationRequest;
import org.stand.springbootecommerce.dto.request.RegisterRequest;
import org.stand.springbootecommerce.dto.response.AuthenticationResponse;
import org.stand.springbootecommerce.dto.response.BaseResponseBody;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.error.BaseException;
import org.stand.springbootecommerce.error.UserNotAuthenticatedException;
import org.stand.springbootecommerce.error.UserNotFoundException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request) throws BadCredentialsException, BaseException;
    BaseResponseBody register(RegisterRequest request) throws BaseException;
    User me() throws UserNotFoundException, UserNotAuthenticatedException;
}

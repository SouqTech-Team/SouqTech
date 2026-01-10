package org.stand.springbootecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.stand.springbootecommerce.dto.UserDTO;
import org.stand.springbootecommerce.dto.request.AuthenticationRequest;
import org.stand.springbootecommerce.dto.request.RegisterRequest;
import org.stand.springbootecommerce.dto.response.AuthenticationResponse;
import org.stand.springbootecommerce.dto.response.BaseResponseBody;
import org.stand.springbootecommerce.error.BaseException;
import org.stand.springbootecommerce.error.UserNotAuthenticatedException;
import org.stand.springbootecommerce.error.UserNotFoundException;
import org.stand.springbootecommerce.service.AuthenticationService;

@Tag(name = "Authentication", description = "Endpoints for user registration and login")
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthenticationController {

        private final AuthenticationService authenticationService;
        private final ModelMapper modelMapper;

        @Operation(summary = "Register on the platform")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "User created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data or email already in use")
        })
        @PostMapping("/register")
        public ResponseEntity<BaseResponseBody> register(
                        @Valid @RequestBody RegisterRequest request) throws BaseException {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(authenticationService.register(request));
        }

        @Operation(summary = "Login to obtain a JWT token")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Authentication successful"),
                        @ApiResponse(responseCode = "401", description = "Invalid credentials")
        })
        @PostMapping("/authenticate")
        public ResponseEntity<AuthenticationResponse> authenticate(
                        @Valid @RequestBody AuthenticationRequest request)
                        throws BadCredentialsException, BaseException {
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(authenticationService.authenticate(request));
        }

        @Operation(summary = "Get current logged-in user information", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User details retrieved"),
                        @ApiResponse(responseCode = "403", description = "Forbidden (Token missing or invalid)")
        })
        @GetMapping("/me")
        public ResponseEntity<UserDTO> me() throws UserNotFoundException, UserNotAuthenticatedException {
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(modelMapper.map(authenticationService.me(), UserDTO.class));
        }

}

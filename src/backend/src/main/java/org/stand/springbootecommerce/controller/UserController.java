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
import org.springframework.web.bind.annotation.*;
import org.stand.springbootecommerce.dto.UserDTO;
import org.stand.springbootecommerce.dto.request.UserUpdateRequest;
import org.stand.springbootecommerce.error.UserNotAuthenticatedException;
import org.stand.springbootecommerce.error.UserNotFoundException;
import org.stand.springbootecommerce.service.UserService;

@Tag(name = "Users", description = "User profile management")
@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UserController {

        private final UserService userService;
        private final ModelMapper modelMapper;

        @Operation(summary = "Update profile information", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Profile updated"),
                        @ApiResponse(responseCode = "403", description = "Unauthorized")
        })
        @PatchMapping
        public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserUpdateRequest updatedUser)
                        throws UserNotFoundException, UserNotAuthenticatedException {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(modelMapper.map(userService.updateUser(updatedUser), UserDTO.class));
        }
}

package com.application.security.application.ports.inbound;

import com.application.security.domain.dto.request.LoginRequest;
import com.application.security.domain.dto.request.RegisterUserRequest;
import com.application.security.domain.dto.response.RegisterUserResponse;
import org.springframework.http.ResponseEntity;

public interface AuthUseCase {

    RegisterUserResponse registerUseCase(RegisterUserRequest userRequest);
    ResponseEntity<?> loginUseCase(LoginRequest loginRequest);
}

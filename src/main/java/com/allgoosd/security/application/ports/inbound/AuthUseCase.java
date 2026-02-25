package com.allgoosd.security.application.ports.inbound;

import com.allgoosd.security.infrastruct.dto.request.LoginRequest;
import com.allgoosd.security.infrastruct.dto.request.RegisterUserRequest;
import com.allgoosd.security.infrastruct.dto.response.LoginResponse;
import com.allgoosd.security.infrastruct.dto.response.RegisterUserResponse;
import org.springframework.http.ResponseEntity;

public interface AuthUseCase {

    RegisterUserResponse registerUseCase(RegisterUserRequest userRequest);
    ResponseEntity<?> loginUseCase(LoginRequest loginRequest);
}

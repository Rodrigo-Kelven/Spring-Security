package com.application.security.infrastruct.adapters.inbound.security;


import com.application.security.application.ports.inbound.AuthUseCase;
import com.application.security.domain.dto.request.LoginRequest;
import com.application.security.domain.dto.request.RegisterUserRequest;
import com.application.security.domain.dto.response.RegisterUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(authUseCase.loginUseCase(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest userRequest){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authUseCase.registerUseCase(userRequest));
    }
}

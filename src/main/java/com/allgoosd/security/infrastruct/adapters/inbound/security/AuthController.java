package com.allgoosd.security.infrastruct.adapters.inbound.security;


import com.allgoosd.security.application.ports.inbound.AuthUseCase;
import com.allgoosd.security.infrastruct.config.security.TokenConfig;
import com.allgoosd.security.infrastruct.dto.request.LoginRequest;
import com.allgoosd.security.infrastruct.dto.request.RegisterUserRequest;
import com.allgoosd.security.infrastruct.dto.response.LoginResponse;
import com.allgoosd.security.infrastruct.dto.response.RegisterUserResponse;
import com.allgoosd.security.infrastruct.persistence.entity.UserEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    private final AuthUseCase authUseCase;


    public AuthController(AuthenticationManager authenticationManager,
                          TokenConfig tokenConfig, AuthUseCase authUseCase
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UsernamePasswordAuthenticationToken usernameAndPass =
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

            Authentication authentication = authenticationManager.authenticate(usernameAndPass);

            UserEntity userEntity = (UserEntity) authentication.getPrincipal();

            String token = tokenConfig.generateToken(userEntity);

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro: Credenciais inválidas. Por favor, verifique seu email e senha.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro inesperado. Tente novamente mais tarde.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest userRequest){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authUseCase.registerUseCase(userRequest));
    }
}

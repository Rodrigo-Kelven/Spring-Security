package com.application.security.application.service;

import com.application.security.application.ports.inbound.AuthUseCase;
import com.application.security.application.ports.outbound.AuthRepositoryPort;
import com.application.security.domain.UserModel;
import com.application.security.infrastruct.config.security.TokenConfig;
import com.application.security.infrastruct.dto.request.LoginRequest;
import com.application.security.infrastruct.dto.response.LoginResponse;
import com.application.security.infrastruct.persistence.entity.UserEntity;
import com.application.security.infrastruct.dto.request.RegisterUserRequest;
import com.application.security.infrastruct.dto.response.RegisterUserResponse;
import com.application.security.infrastruct.persistence.repository.UserRepositoryConcret;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthUseCase {

    private final UserRepositoryConcret userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    private final AuthRepositoryPort authRepositoryPort;


    public AuthService(UserRepositoryConcret userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       TokenConfig tokenConfig,
                       AuthRepositoryPort authRepositoryPort
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
        this.authRepositoryPort = authRepositoryPort;
    }


    @Override
    public RegisterUserResponse registerUseCase(RegisterUserRequest userRequest){
        if (userRepository.existsByEmail(userRequest.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterUserResponse("Erro: Usuário já registrado com este email", userRequest.email())).getBody();
        }

        UserModel userModel = new UserModel(
                userRequest.name(),
                userRequest.email(),
                passwordEncoder.encode(userRequest.password())
        );

        UserEntity user = authRepositoryPort.save(userModel);

        return new RegisterUserResponse(
                user.getName(),
                user.getEmail()
        );

    }


    @Override
    public ResponseEntity<?> loginUseCase(LoginRequest loginRequest){
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
}

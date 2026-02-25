package com.allgoosd.security.application.service;

import com.allgoosd.security.application.ports.inbound.AuthUseCase;
import com.allgoosd.security.application.ports.outbound.AuthRepositoryPort;
import com.allgoosd.security.domain.UserModel;
import com.allgoosd.security.infrastruct.persistence.entity.UserEntity;
import com.allgoosd.security.infrastruct.dto.request.RegisterUserRequest;
import com.allgoosd.security.infrastruct.dto.response.RegisterUserResponse;
import com.allgoosd.security.infrastruct.persistence.repository.UserRepositoryConcret;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthUseCase {

    private final UserRepositoryConcret userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthRepositoryPort authRepositoryPort;


    public AuthService(UserRepositoryConcret userRepository, PasswordEncoder passwordEncoder, AuthRepositoryPort authRepositoryPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}

package com.allgoosd.security.controller;


import com.allgoosd.security.config.TokenConfig;
import com.allgoosd.security.dto.request.LoginRequest;
import com.allgoosd.security.dto.request.RegisterUserRequest;
import com.allgoosd.security.dto.response.LoginResponse;
import com.allgoosd.security.dto.response.RegisterUserResponse;
import com.allgoosd.security.model.User;
import com.allgoosd.security.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;


    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          TokenConfig tokenConfig
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UsernamePasswordAuthenticationToken usernameAndPass =
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

            Authentication authentication = authenticationManager.authenticate(usernameAndPass);

            User user = (User) authentication.getPrincipal();

            String token = tokenConfig.generateToken(user);

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
        if (userRepository.existsByEmail(userRequest.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterUserResponse("Erro: Usuário já registrado com este email", userRequest.email()));
        }

        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(userRequest.password()));
        newUser.setEmail(userRequest.email());
        newUser.setName(userRequest.name());

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterUserResponse(newUser.getName(), newUser.getEmail()));
    }
}

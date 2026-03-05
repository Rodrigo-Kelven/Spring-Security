package com.application.security.infrastruct.adapters.outbound;

import com.application.security.application.ports.outbound.AuthRepositoryPort;
import com.application.security.domain.UserModel;
import com.application.security.infrastruct.persistence.entity.UserEntity;
import com.application.security.infrastruct.persistence.repository.UserRepositoryConcret;
import org.springframework.stereotype.Service;

@Service
public class AuthRepositoryPortImpl implements AuthRepositoryPort {

    private final UserRepositoryConcret userRepositoryConcret;

    public AuthRepositoryPortImpl(UserRepositoryConcret userRepositoryConcret) {
        this.userRepositoryConcret = userRepositoryConcret;
    }

    @Override
    public UserEntity save(UserModel userModel){
        UserEntity user = new UserEntity(
                userModel.getId(),
                userModel.getName(),
                userModel.getEmail(),
                userModel.getPassword()
        );

        return userRepositoryConcret.save(user);
    }
}

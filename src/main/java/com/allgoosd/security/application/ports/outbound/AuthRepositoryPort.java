package com.allgoosd.security.application.ports.outbound;

import com.allgoosd.security.domain.UserModel;
import com.allgoosd.security.infrastruct.persistence.entity.UserEntity;

public interface AuthRepositoryPort {

    UserEntity save(UserModel userModel);
}

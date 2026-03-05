package com.application.security.application.ports.outbound;

import com.application.security.domain.UserModel;
import com.application.security.infrastruct.persistence.entity.UserEntity;

public interface AuthRepositoryPort {

    UserEntity save(UserModel userModel);
}

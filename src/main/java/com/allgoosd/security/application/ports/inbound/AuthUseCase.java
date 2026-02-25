package com.allgoosd.security.application.ports.inbound;

import com.allgoosd.security.infrastruct.dto.request.RegisterUserRequest;
import com.allgoosd.security.infrastruct.dto.response.RegisterUserResponse;

public interface AuthUseCase {

    RegisterUserResponse registerUseCase(RegisterUserRequest userRequest);
}

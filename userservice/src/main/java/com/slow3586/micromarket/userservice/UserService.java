package com.slow3586.micromarket.userservice;


import com.slow3586.micromarket.api.QrCodeUtils;
import com.slow3586.micromarket.api.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    QrCodeUtils qrCodeUtils;

    public UserDto findById(UUID uuid) {
        return userRepository.findById(uuid)
            .orElseThrow(() -> new IllegalArgumentException(
                "Customer with UUID " + uuid + " does not exist!"));
    }
}

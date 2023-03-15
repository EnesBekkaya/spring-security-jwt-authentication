package com.example.securityexample.service.concretes;

import com.example.securityexample.dto.UserDto;
import com.example.securityexample.dto.UserRequest;
import com.example.securityexample.dto.UserResponse;
import com.example.securityexample.enums.Role;
import com.example.securityexample.model.User;
import com.example.securityexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserResponse save(UserDto userDto) {
        User user=User.builder().username(userDto.getUsername())
                        .password(passwordEncoder.encode(userDto.getPassword())).nameSurname(userDto.getNameSurname())
                        .role(Role.USER).build();

        userRepository.save(user);

        var token=jwtService.generateToke(user);
        return UserResponse.builder().token(token).build();

    }

    public UserResponse auth(UserRequest userRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequest.getUsername(),
                        userRequest.getPassword()
                )
        );

        User user=userRepository.findByUsername(userRequest.getUsername()).orElseThrow();
        String token=jwtService.generateToke(user);
        return UserResponse.builder().token(token).build();
    }
}

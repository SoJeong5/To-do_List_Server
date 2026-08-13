package com.example.todoapp.user.service;

import com.example.todoapp.common.exception.BusinessException;
import com.example.todoapp.common.exception.ErrorCode;
import com.example.todoapp.common.response.ApiResponse;
import com.example.todoapp.user.dto.*;
import com.example.todoapp.user.entity.User;
import com.example.todoapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//    private final UserService userService;

    // 회원가입
    public void signup(SignupRequest request) {

        // 아이디 중복 확인
        if(userRepository.findByLoginId(request.getLoginId()).isPresent()) {
//            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
            throw new BusinessException(ErrorCode.LOGIN_ID_DUPLICATED);
        }

        // 닉네임 중복 확인
        if(userRepository.findByNickname(request.getNickname()).isPresent()) {
//            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
        }

        // User Entity 생성
        User user = new User(
                request.getLoginId(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getNickname(),
                request.getEmail(),
                request.getPhone(),
                request.getBirth()
        );

        // DB 저장
        userRepository.save(user);
    }

    // 아이디 중복 확인
    public UserCheckResponse checkLoginId(String loginId) {

        boolean exists = userRepository
                .findByLoginId(loginId)
                .isPresent();

        if(exists) {
            return new UserCheckResponse(
                    false,
                    "이미 존재하는 아이디입니다"
            );
        }

        return new UserCheckResponse(
                true,
                "사용 가능한 아이디입니다"
        );
    }

    // 닉네임 중복 확인
    public UserCheckResponse checkNickname(String nickname) {
        boolean exists = userRepository
                .findByNickname(nickname)
                .isPresent();

        if(exists) {
            return new UserCheckResponse(
                    false,
                    "이미 존재하는 닉네임입니다"
            );
        }

            return new UserCheckResponse(
                    true,
                    "사용 가능한 닉네임입니다"
            );
    }

    // 로그인
    public UserResponse login(LoginRequest request) {

        User user = userRepository
                .findByLoginId(request.getLoginId())
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.LOGIN_FAILED)
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getBirth()
        );
    }

    // 회원정보 조회
    public UserResponse getUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );

        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getBirth()
        );
    }

    // 닉네임 변경
    public void changeNickname(
            Long id,
            ChangeNicknameRequest request
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );

        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new BusinessException(
                    ErrorCode.NICKNAME_DUPLICATED
            );
        }

        user.changeNickname(request.getNickname());
        userRepository.save(user);
    }

    // 이메일 변경
    public void changeEmail(
            Long id,
            ChangeEmailRequest request
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );

        user.changeEmail(request.getEmail());
        userRepository.save(user);
    }
}

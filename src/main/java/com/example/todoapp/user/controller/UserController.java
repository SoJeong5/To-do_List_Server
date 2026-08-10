package com.example.todoapp.user.controller;

import com.example.todoapp.common.response.ApiResponse;
import com.example.todoapp.user.dto.*;
import com.example.todoapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupRequest request) {
        userService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "회원가입 성공",
                                null
                        )
                );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        UserResponse userResponse = userService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiResponse<>(
                                true,
                                "로그인 성공",
                                userResponse
                        )
                );
    }
//    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
//        userService.signup(request);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body("회원가입 성공");
//    }

    @GetMapping("/check-login-id")
    public ResponseEntity<UserCheckResponse> checkLoginId(@RequestParam String loginId) {
        return ResponseEntity.ok(userService.checkLoginId(loginId));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<UserCheckResponse> checkUserNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.checkNickname(nickname));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id) {

        UserResponse userResponse = userService.getUser(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiResponse<>(
                                true,
                                "회원정보 조회 성공",
                                userResponse
                        )
                );
    }

    @PatchMapping("/{id}/nickname")
    public ResponseEntity<ApiResponse> changeNickname(
            @PathVariable Long id,
            @RequestBody ChangeNicknameRequest request
    ) {
        userService.changeNickname(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "닉네임 변경 성공",
                        null
                )
        );
    }

//    @PatchMapping("/{id}/email")
//    public ResponseEntity<ApiResponse> changeEmail(
//            @PathVariable Long id,
//            @RequestBody ChangeEmailRequest request
//    ) {
//        userService.changeEmail(id, request);
//
//        return ResponseEntity.ok(
//                new ApiResponse<>(
//                        true,
//                        "이메일 변경 성공",
//                        null
//                )
//        );
//    }

}

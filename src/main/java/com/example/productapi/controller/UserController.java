package com.example.productapi.controller;

import com.example.productapi.dto.UserDto;
import com.example.productapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자(User) 관련 API를 처리하는 컨트롤러
 * <p>
 * 이 컨트롤러는 사용자의 생성, 조회, 수정, 삭제 등 사용자와 관련된 모든 작업을 담당합니다.
 * 사용자 목록 조회 및 이메일 중복 확인 기능을 제공합니다.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    /**
     * 모든 사용자 목록을 조회합니다.
     * 
     * @return 전체 사용자 목록과 HTTP 200 OK 응답
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 새로운 사용자를 생성합니다.
     * 
     * @param userDto 사용자 생성에 필요한 정보가 담긴 DTO 객체
     * @return 생성된 사용자 정보와 HTTP 201 Created 응답
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    /**
     * ID로 특정 사용자를 조회합니다.
     * 
     * @param id 조회할 사용자의 ID
     * @return 사용자 정보와 HTTP 200 OK 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * 이메일로 특정 사용자를 조회합니다.
     * 
     * @param email 조회할 사용자의 이메일
     * @return 사용자 정보와 HTTP 200 OK 응답
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        UserDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    /**
     * 이메일 중복 여부를 확인합니다.
     * 
     * @param email 중복 확인할 이메일
     * @return 이메일 존재 여부(true/false)와 HTTP 200 OK 응답
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * 사용자 정보를 수정합니다.
     * 
     * @param id 수정할 사용자의 ID
     * @param userDto 수정할 내용이 담긴 DTO 객체
     * @return 수정된 사용자 정보와 HTTP 200 OK 응답
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id, 
            @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * 특정 사용자를 삭제합니다.
     * 
     * @param id 삭제할 사용자의 ID
     * @return HTTP 204 No Content 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
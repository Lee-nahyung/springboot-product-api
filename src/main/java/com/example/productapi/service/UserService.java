package com.example.productapi.service;

import com.example.productapi.domain.entity.User;
import com.example.productapi.domain.repository.UserRepository;
import com.example.productapi.dto.UserDto;
import com.example.productapi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // Create - 사용자 생성
    @Transactional
    public UserDto createUser(UserDto userDto) {
        userDto.setId(null); // 새 사용자 생성 시 ID 필드를 null로 설정
        User savedUser = userRepository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(savedUser);
    }

    // Read - 모든 사용자 조회
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    // Read - 특정 ID로 사용자 조회
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return userMapper.toUserDto(user);
    }

    // Read - 이메일로 사용자 조회
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with email: " + email));
        return userMapper.toUserDto(user);
    }

    // Update - 사용자 정보 수정
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        // 기존 사용자가 존재하는지 확인
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
        
        // ID 값 설정하여 업데이트
        userDto.setId(id);
        User updatedUser = userRepository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(updatedUser);
    }

    // Delete - 사용자 삭제
    @Transactional
    public void deleteUser(Long id) {
        // 기존 사용자가 존재하는지 확인
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Email 존재 여부 확인
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
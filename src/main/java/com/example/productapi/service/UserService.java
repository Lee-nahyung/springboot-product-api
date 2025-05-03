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

/**
 * 사용자(User) 관련 비즈니스 로직을 처리하는 서비스
 * <p>
 * 이 서비스는 사용자의 생성, 조회, 수정, 삭제 및 이메일 중복 확인과 관련된 비즈니스 로직을 구현합니다.
 * UserRepository를 통해 데이터베이스와 상호작용하며,
 * UserMapper를 사용하여 엔티티와 DTO 간의 변환을 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * 새로운 사용자를 생성합니다.
     * <p>
     * 입력받은 UserDto를 바탕으로 새로운 사용자를 데이터베이스에 저장합니다.
     * 이메일 중복 확인 등의 검증이 필요할 수 있습니다.
     * </p>
     * 
     * @param userDto 생성할 사용자 정보가 담긴 DTO
     * @return 생성된 사용자 정보(ID 포함)
     */
    @Transactional
    public UserDto createUser(UserDto userDto) {
        userDto.setId(null); // 새 사용자 생성 시 ID 필드를 null로 설정
        User savedUser = userRepository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(savedUser);
    }

    /**
     * 모든 사용자 목록을 조회합니다.
     * <p>
     * 데이터베이스에 저장된 모든 사용자 정보를 가져와 DTO 리스트로 반환합니다.
     * </p>
     * 
     * @return 전체 사용자 목록
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    /**
     * ID를 기준으로 특정 사용자를 조회합니다.
     * <p>
     * 입력받은 ID에 해당하는 사용자를 찾아 DTO로 반환합니다.
     * 해당 ID의 사용자가 없는 경우 예외가 발생합니다.
     * </p>
     * 
     * @param id 조회할 사용자의 ID
     * @return 조회된 사용자 정보
     * @throws ResponseStatusException 사용자를 찾을 수 없는 경우 (404 Not Found)
     */
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return userMapper.toUserDto(user);
    }

    /**
     * 이메일을 기준으로 특정 사용자를 조회합니다.
     * <p>
     * 입력받은 이메일에 해당하는 사용자를 찾아 DTO로 반환합니다.
     * 해당 이메일의 사용자가 없는 경우 예외가 발생합니다.
     * </p>
     * 
     * @param email 조회할 사용자의 이메일
     * @return 조회된 사용자 정보
     * @throws ResponseStatusException 사용자를 찾을 수 없는 경우 (404 Not Found)
     */
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with email: " + email));
        return userMapper.toUserDto(user);
    }

    /**
     * 사용자 정보를 수정합니다.
     * <p>
     * 입력받은 ID의 사용자를 찾아 DTO의 정보로 업데이트합니다.
     * 사용자 이름, 이메일, 비밀번호 등의 정보가 변경될 수 있습니다.
     * </p>
     * 
     * @param id 수정할 사용자의 ID
     * @param userDto 수정할 내용이 담긴 DTO
     * @return 수정된 사용자 정보
     * @throws ResponseStatusException 사용자를 찾을 수 없는 경우 (404 Not Found)
     */
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

    /**
     * 특정 사용자를 삭제합니다.
     * <p>
     * 입력받은 ID의 사용자를 데이터베이스에서 제거합니다.
     * 삭제 전 사용자가 존재하는지 확인하여 없는 경우 예외가 발생합니다.
     * </p>
     * 
     * @param id 삭제할 사용자의 ID
     * @throws ResponseStatusException 사용자를 찾을 수 없는 경우 (404 Not Found)
     */
    @Transactional
    public void deleteUser(Long id) {
        // 기존 사용자가 존재하는지 확인
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * 이메일 중복 여부를 확인합니다.
     * <p>
     * 입력받은 이메일이 이미 시스템에 등록되어 있는지 확인합니다.
     * 사용자 등록 시 이메일 중복 방지를 위해 사용됩니다.
     * </p>
     * 
     * @param email 중복 확인할 이메일
     * @return 이메일 존재 여부 (true: 이미 존재함, false: 존재하지 않음)
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
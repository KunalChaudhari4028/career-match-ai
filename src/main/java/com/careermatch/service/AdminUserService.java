package com.careermatch.service;

import com.careermatch.dto.AdminUserResponseDto;
import com.careermatch.dto.UpdateUserRoleRequest;
import com.careermatch.entity.User;
import com.careermatch.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminUserService {
    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AdminUserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<AdminUserResponseDto> adminUserResponseDtos = new ArrayList<>();
        for (User user : users) {
            adminUserResponseDtos.add(convert1(user));
        }
        return adminUserResponseDtos;

//        return userRepository.findAll()
//                .stream()
//                .map(this::convertToDto)
//                .toList();
    }
    public AdminUserResponseDto getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with ID: " + id
                        )
                );

        return convert1(user);
    }
    public AdminUserResponseDto updateUserRole(Long id, UpdateUserRoleRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with ID: " + id
                        )
                );

        String role = request.role();

        if (role == null || (!role.equals("USER") && !role.equals("ADMIN"))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid role. Allowed roles are USER and ADMIN"
            );
        }

        user.setRole(role);

        User updatedUser = userRepository.save(user);

        return convert1(updatedUser);
    }
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with ID: " + id
                        )
                );

        userRepository.delete(user);
    }
//    private AdminUserResponseDto convertToDto(User user) {
//
//        return new AdminUserResponseDto(
//                user.getId(),
//                user.getFullName(),
//                user.getEmail(),
//                user.getRole(),
//                user.getCreatedAt()
//        );
//    }
    private AdminUserResponseDto convert1(User user) {
        AdminUserResponseDto adminUserResponseDto = new AdminUserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
                );
        return adminUserResponseDto;
    }
}

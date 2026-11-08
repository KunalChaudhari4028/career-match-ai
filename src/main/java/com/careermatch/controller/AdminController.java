
package com.careermatch.controller;

import com.careermatch.dto.AdminUserResponseDto;
import com.careermatch.dto.UpdateUserRoleRequest;
import com.careermatch.service.AdminUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminUserService  adminUserService;
    public AdminController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/dashboard")
    public Map<String, String> getAdminDashboard() {

        return Map.of(
                "message", "Welcome to Admin Dashboard",
                "role", "ADMIN"
        );
    }

    @GetMapping("/users")
    public List<AdminUserResponseDto> getAllUsers() {
        return adminUserService.getAllUsers();
    }
    @GetMapping("/users/{id}")
    public AdminUserResponseDto getUserById(@PathVariable Long id) {
        return adminUserService.getUserById(id);
    }
    @PutMapping("/users/{id}/role")
    public AdminUserResponseDto updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateUserRoleRequest request) {
        return adminUserService.updateUserRole(id, request);
    }
    @DeleteMapping("/users/{id}")
    public Map<String, String> deleteUser(@PathVariable Long id) {

        adminUserService.deleteUser(id);

        return Map.of(
                "message", "User deleted successfully"
        );
    }

}

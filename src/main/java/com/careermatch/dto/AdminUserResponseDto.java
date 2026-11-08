
package com.careermatch.dto;

import java.time.LocalDateTime;

public record AdminUserResponseDto(
        Long id,
        String fullName,
        String email,
        String role,
        LocalDateTime createdAt
) {

}

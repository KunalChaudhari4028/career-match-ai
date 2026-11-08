package com.careermatch.dto;

import java.time.LocalDateTime;

public record ResumeResponseDto(
        Long id,
        String fileName,
        LocalDateTime uploadedAt
) {
}
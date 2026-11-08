package com.careermatch.controller;

import com.careermatch.dto.ResumeResponseDto;
import com.careermatch.service.ResumeService;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResumeResponseDto uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String email = authentication.getName();

        return resumeService.uploadResume(file, email);
    }
    @GetMapping
    public List<ResumeResponseDto> getAllResumes(Authentication authentication) {

        String email = authentication.getName();

        return resumeService.getAllResumes(email);
    }
    @GetMapping("/{id}")
    public ResumeResponseDto getResumeById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        return resumeService.getResumeById(id, email);
    }
    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadResume(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        byte[] resumeData = resumeService.downloadResume(id, email);

        ByteArrayResource resource = new ByteArrayResource(resumeData);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"resume.pdf\""
                )
                .body(resource);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteResume(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        resumeService.deleteResume(id, email);

        return ResponseEntity.ok("Resume deleted successfully");
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResumeResponseDto updateResume(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String email = authentication.getName();

        return resumeService.updateResume(id, file, email);
    }
}
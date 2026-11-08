
package com.careermatch.service;

import com.careermatch.dto.ResumeResponseDto;
import com.careermatch.entity.Resume;
import com.careermatch.entity.User;
import com.careermatch.repository.ResumeRepository;
import com.careermatch.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository) {

        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    public ResumeResponseDto uploadResume(MultipartFile file, String email) {

        // Check whether file is empty
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a file"
            );
        }

        // Validate PDF file extension
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null ||
                !originalFileName.toLowerCase().endsWith(".pdf")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PDF files are allowed"
            );
        }

        // Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        try {
            // Create upload directory
            Path uploadDirectory = Paths.get("uploads/resumes");
            Files.createDirectories(uploadDirectory);

            // Generate unique file name
            String uniqueFileName = UUID.randomUUID() + ".pdf";

            // Store PDF file
            Path filePath = uploadDirectory.resolve(uniqueFileName);
            file.transferTo(filePath);

            // Save resume details in database
            Resume resume = new Resume();
            resume.setUser(user);
            resume.setFileName(originalFileName);
            resume.setFilePath(filePath.toString());

            Resume savedResume = resumeRepository.save(resume);

            // Return response DTO
            return new ResumeResponseDto(
                    savedResume.getId(),
                    savedResume.getFileName(),
                    savedResume.getUploadedAt()
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to upload resume"
            );
        }
    }
    public List<ResumeResponseDto> getAllResumes(String email) {

        List<Resume> resumes = resumeRepository.findByUserEmail(email);

        List<ResumeResponseDto> resumeResponseDtos = new ArrayList<>();

        for (Resume resume : resumes) {

            ResumeResponseDto dto = new ResumeResponseDto(
                    resume.getId(),
                    resume.getFileName(),
                    resume.getUploadedAt()
            );

            resumeResponseDtos.add(dto);
        }

        return resumeResponseDtos;
    }

    public ResumeResponseDto getResumeById(Long id, String email) {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Resume not found with ID: " + id
                        )
                );

        if (!resume.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access this resume"
            );
        }

        return new ResumeResponseDto(
                resume.getId(),
                resume.getFileName(),
                resume.getUploadedAt()
        );
    }
    public byte[] downloadResume(Long id, String email) {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Resume not found"
                        )
                );

        if (!resume.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to download this resume"
            );
        }

        try {
            Path filePath = Paths.get(resume.getFilePath());

            if (!Files.exists(filePath)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Resume file not found"
                );
            }

            return Files.readAllBytes(filePath);

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to download resume"
            );
        }
    }
    public void deleteResume(Long id, String email) {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Resume not found"
                        )
                );

        if (!resume.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to delete this resume"
            );
        }

        try {
            Path filePath = Paths.get(resume.getFilePath());

            Files.deleteIfExists(filePath);

            resumeRepository.delete(resume);

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to delete resume file"
            );
        }
    }
    public ResumeResponseDto updateResume(Long id, MultipartFile file, String email) {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Resume not found"
                        )
                );

        if (!resume.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to update this resume"
            );
        }

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a file"
            );
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null ||
                !originalFileName.toLowerCase().endsWith(".pdf")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PDF files are allowed"
            );
        }

        try {
            Path uploadDirectory = Paths.get("uploads/resumes");
            Files.createDirectories(uploadDirectory);

            String uniqueFileName = UUID.randomUUID() + ".pdf";
            Path newFilePath = uploadDirectory.resolve(uniqueFileName);

            file.transferTo(newFilePath);

            Path oldFilePath = Paths.get(resume.getFilePath());
            Files.deleteIfExists(oldFilePath);

            resume.setFileName(originalFileName);
            resume.setFilePath(newFilePath.toString());

            Resume updatedResume = resumeRepository.save(resume);

            return new ResumeResponseDto(
                    updatedResume.getId(),
                    updatedResume.getFileName(),
                    updatedResume.getUploadedAt()
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to update resume"
            );
        }
    }
}

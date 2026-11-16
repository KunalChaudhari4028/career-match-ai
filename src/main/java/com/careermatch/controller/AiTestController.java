
package com.careermatch.controller;

import com.careermatch.dto.AiSkillResponseDto;
import com.careermatch.service.AiResumeSkillExtractionService;
import com.careermatch.service.AiTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiTestController {

    private final AiTestService aiTestService;
    private final AiResumeSkillExtractionService  aiResumeSkillExtractionService;

    public AiTestController(AiTestService aiTestService, AiResumeSkillExtractionService aiResumeSkillExtractionService) {
        this.aiTestService = aiTestService;
        this.aiResumeSkillExtractionService = aiResumeSkillExtractionService;
    }

    @GetMapping("/test-resume")
    public AiSkillResponseDto testResumeExtraction() {

        String resumeText = """
            Developed REST APIs using Java and Spring Boot.
            Worked with MySQL, Hibernate,
            Spring Security and Git.
            Built an Employee Management System.
            """;

        return aiResumeSkillExtractionService.extractSkills(resumeText);
    }
}

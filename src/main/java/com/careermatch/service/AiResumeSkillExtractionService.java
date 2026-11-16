
package com.careermatch.service;

import com.careermatch.dto.AiSkillResponseDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class AiResumeSkillExtractionService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AiResumeSkillExtractionService(
            ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {

        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public AiSkillResponseDto extractSkills(String resumeText) {

        String prompt = """
                Extract all technical skills from the following resume.

                Return only valid JSON in this format:
                {
                    "skills": [
                        "Java",
                        "Spring Boot",
                        "MySQL"
                    ]
                }

                Do not include explanations.
                Do not invent skills.
                Include every technical skill mentioned.

                Resume text:
                %s
                """.formatted(resumeText);

        String response= chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        try {
            return objectMapper.readValue(
                    response,
                    AiSkillResponseDto.class
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to process AI skill extraction response"
            );
        }
    }
}

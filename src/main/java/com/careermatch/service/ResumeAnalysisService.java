
package com.careermatch.service;

import com.careermatch.entity.Resume;
import com.careermatch.entity.Skill;
import com.careermatch.entity.DiscoveredSkill;
import com.careermatch.repository.ResumeRepository;
import com.careermatch.repository.SkillRepository;
import com.careermatch.repository.DiscoveredSkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final DiscoveredSkillRepository discoveredSkillRepository;
    private final ResumeParserService resumeParserService;
    private final AiResumeSkillExtractionService aiResumeSkillExtractionService;

    public ResumeAnalysisService(
            ResumeRepository resumeRepository,
            SkillRepository skillRepository,
            DiscoveredSkillRepository discoveredSkillRepository,
            ResumeParserService resumeParserService,
            AiResumeSkillExtractionService aiResumeSkillExtractionService) {

        this.resumeRepository = resumeRepository;
        this.skillRepository = skillRepository;
        this.discoveredSkillRepository = discoveredSkillRepository;
        this.resumeParserService = resumeParserService;
        this.aiResumeSkillExtractionService = aiResumeSkillExtractionService;
    }

    @Transactional
    public List<String> analyzeSkills(Long resumeId, String email) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Resume not found"));

        if (!resume.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You cannot access this resume");
        }

        // Step 1: Extract text from PDF
        String extractedText =
                resumeParserService.extractText(resume.getFilePath());

        // Step 2: Extract skills using Regex
        List<String> regexSkills =
                resumeParserService.extractSkills(extractedText);

        // Step 3: Extract skills using Qwen AI
        List<String> aiSkills =
                aiResumeSkillExtractionService
                        .extractSkills(extractedText)
                        .skills();

        // Step 4: Merge both skill lists
        Set<String> skillNames = new LinkedHashSet<>();

        for (String skillName : regexSkills) {
            if (skillName != null && !skillName.isBlank()) {
                skillNames.add(skillName.trim());
            }
        }

        for (String skillName : aiSkills) {
            if (skillName != null && !skillName.isBlank()) {
                skillNames.add(skillName.trim());
            }
        }

        // Step 5: Get existing skills from MySQL
        Set<Skill> resumeSkills = new HashSet<>();
        List<Skill> availableSkills = skillRepository.findAll();

        // Step 6: Match existing skills and save missing skills
        for (String skillName : skillNames) {

            String normalizedName =
                    skillName.toLowerCase(Locale.ROOT);

            boolean skillExists = false;

            for (Skill skill : availableSkills) {

                if (skill.getName().equalsIgnoreCase(skillName)) {
                    resumeSkills.add(skill);
                    skillExists = true;
                    break;
                }
            }

            // Skill is missing from the approved catalog
            if (!skillExists) {

                DiscoveredSkill discoveredSkill =
                        discoveredSkillRepository
                                .findByNormalizedName(normalizedName)
                                .orElseGet(() -> {
                                    DiscoveredSkill newSkill =
                                            new DiscoveredSkill();

                                    newSkill.setName(skillName);
                                    newSkill.setNormalizedName(normalizedName);

                                    return newSkill;
                                });

                discoveredSkill.getResumes().add(resume);

                discoveredSkillRepository.save(discoveredSkill);
            }
        }

        // Step 7: Save approved skills against the resume
        resume.setSkills(resumeSkills);
        resumeRepository.save(resume);

        // Return all extracted skills, including discovered skills
        return List.copyOf(skillNames);
    }
}

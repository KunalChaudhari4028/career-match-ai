
package com.careermatch.service;

import com.careermatch.entity.Resume;
import com.careermatch.entity.Skill;
import com.careermatch.repository.ResumeRepository;
import com.careermatch.repository.SkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final ResumeParserService resumeParserService;

    public ResumeAnalysisService(
            ResumeRepository resumeRepository,
            SkillRepository skillRepository,
            ResumeParserService resumeParserService) {

        this.resumeRepository = resumeRepository;
        this.skillRepository = skillRepository;
        this.resumeParserService = resumeParserService;
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

        String extractedText =
                resumeParserService.extractText(resume.getFilePath());

        List<String> extractedSkills =
                resumeParserService.extractSkills(extractedText);

        Set<String> skillNames = new HashSet<>();

        for (String skillName : extractedSkills) {
            skillNames.add(skillName.toLowerCase());
        }

        Set<Skill> resumeSkills = new HashSet<>();

        List<Skill> availableSkills = skillRepository.findAll();

        for (Skill skill : availableSkills) {

            if (skillNames.contains(skill.getName().toLowerCase())) {
                resumeSkills.add(skill);
            }
        }

        resume.setSkills(resumeSkills);

        resumeRepository.save(resume);

        return extractedSkills;
    }
}

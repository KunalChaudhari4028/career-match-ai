package com.careermatch.service;

import com.careermatch.entity.Skill;
import com.careermatch.repository.SkillRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ResumeParserService {
    private final SkillRepository skillRepository;
    public ResumeParserService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public String extractText(String filePath) {

        try (PDDocument document =
                     Loader.loadPDF(new File(filePath))) {

            PDFTextStripper pdfTextStripper =
                    new PDFTextStripper();

            String extractedText =
                    pdfTextStripper.getText(document);

            return extractedText;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to extract text from PDF"
            );
        }
    }

    public List<String> extractSkills(String extractedText) {

        List<Skill> availableSkills = skillRepository.findAll();

        List<String> foundSkills = new ArrayList<>();

        String text = extractedText.toLowerCase();

        for (Skill skill : availableSkills) {

            String skillName = skill.getName().toLowerCase();

            Pattern pattern = Pattern.compile(
                    "(?<![a-z0-9])" + Pattern.quote(skillName) + "(?![a-z0-9])"
            );

            if (pattern.matcher(text).find()) {
                foundSkills.add(skill.getName());
            }
        }

        return foundSkills;
    }

}
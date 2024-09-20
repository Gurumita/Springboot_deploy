package com.revhire.userservice.Mockito;
import com.revhire.userservice.Services.ResumeService;
import com.revhire.userservice.dto.Resume;
import com.revhire.userservice.models.*;
import com.revhire.userservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResumeServiceTest {

    @InjectMocks
    private ResumeService resumeService;

    @Mock
    private SkillsRepository skillsRepository;
    @Mock
    private EducationRepository educationRepository;
    @Mock
    private ExperienceRepository experienceRepository;
    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private SummaryRepository summaryRepository;
    @Mock
    private UserRepository userRepository;

    private User user;
    private Skills skill;
    private Education education;
    private Experience experience;
    private Language language;
    private Summary summary;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        user = new User();
        user.setUserId(1L);

        skill = new Skills();
        skill.setUser(user);

        education = new Education();
        education.setUser(user);

        experience = new Experience();
        experience.setUser(user);

        language = new Language();
        language.setUser(user);

        summary = new Summary();
        summary.setUser(user);
    }

    @Test
    public void getResumeByUserId_ShouldReturnResume_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(skillsRepository.findAll()).thenReturn(List.of(skill));
        when(educationRepository.findAll()).thenReturn(List.of(education));
        when(experienceRepository.findAll()).thenReturn(List.of(experience));
        when(languageRepository.findAll()).thenReturn(List.of(language));
        when(summaryRepository.findAll()).thenReturn(List.of(summary));

        Resume resume = resumeService.getResumeByUserId(1L);

        assertNotNull(resume);
        assertEquals(user, resume.getUser());
        assertEquals(1, resume.getSkills().size());
        assertEquals(1, resume.getEducation().size());
        assertEquals(1, resume.getExperience().size());
        assertEquals(1, resume.getLanguages().size());
        assertEquals(summary, resume.getSummary());
    }



    @Test
    public void updateResume_ShouldUpdateAllComponents_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(skillsRepository.findAll()).thenReturn(new ArrayList<>());
        when(educationRepository.findAll()).thenReturn(new ArrayList<>());
        when(experienceRepository.findAll()).thenReturn(new ArrayList<>());
        when(languageRepository.findAll()).thenReturn(new ArrayList<>());
        when(summaryRepository.findAll()).thenReturn(new ArrayList<>());

        Resume resume = new Resume();
        resume.setSkills(List.of(skill));
        resume.setEducation(List.of(education));
        resume.setExperience(List.of(experience));
        resume.setLanguages(List.of(language));
        resume.setSummary(summary);

        resumeService.updateResume(1L, resume);

        verify(skillsRepository, times(1)).save(skill);
        verify(educationRepository, times(1)).save(education);
        verify(experienceRepository, times(1)).save(experience);
        verify(languageRepository, times(1)).save(language);
        verify(summaryRepository, times(1)).save(summary);
    }

    @Test
    public void updateResume_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Resume resume = new Resume();

        assertThrows(IllegalArgumentException.class, () -> {
            resumeService.updateResume(1L, resume);
        });
    }
}
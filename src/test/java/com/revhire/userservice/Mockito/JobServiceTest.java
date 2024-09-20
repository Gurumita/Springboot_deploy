package com.revhire.userservice.Mockito;
import com.revhire.userservice.Services.JobService;
import com.revhire.userservice.enums.ApplicationStatus;
import com.revhire.userservice.models.Application;
import com.revhire.userservice.models.Job;
import com.revhire.userservice.models.User;
import com.revhire.userservice.repository.ApplicationRepository;
import com.revhire.userservice.repository.EmployerRepository;
import com.revhire.userservice.repository.JobRepository;
import com.revhire.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JobServiceTest {

    @InjectMocks
    private JobService jobService;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createJob_ShouldReturnSavedJob() {
        Job job = new Job();
        job.setJobTitle("Software Engineer");
        job.setJobDescription("Develop software applications");

        when(jobRepository.save(any(Job.class))).thenReturn(job);

        Job createdJob = jobService.createJob(job);

        assertNotNull(createdJob);
        assertEquals("Software Engineer", createdJob.getJobTitle());
        verify(jobRepository, times(1)).save(job);
    }

    @Test
    public void getJobById_ShouldReturnJob_WhenJobExists() {
        Job job = new Job();
        job.setJobId(1L);
        job.setJobTitle("Software Engineer");

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        Optional<Job> foundJob = jobService.getJobById(1L);

        assertTrue(foundJob.isPresent());
        assertEquals("Software Engineer", foundJob.get().getJobTitle());
    }

    @Test
    public void getJobById_ShouldReturnEmpty_WhenJobDoesNotExist() {
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Job> foundJob = jobService.getJobById(1L);

        assertFalse(foundJob.isPresent());
    }

    @Test
    public void applyForJob_ShouldAddApplicationAndUpdateJob() {
        Job job = new Job();
        job.setJobId(1L);
        job.setJobTitle("Software Engineer");
        job.setApplicants(new HashSet<>());

        User user = new User();
        user.setUserId(1L);
        user.setFirstName("John");

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        jobService.applyForJob(1L, 1L);

        assertEquals(1, job.getApplicants().size());
        assertTrue(job.getApplicants().contains(user));

        verify(applicationRepository, times(1)).save(any(Application.class));
        verify(jobRepository, times(1)).save(job);
    }

    @Test
    public void withdrawApplication_ShouldUpdateApplicationStatus() {
        Application application = new Application();
        application.setStatus(ApplicationStatus.APPLIED);

        when(applicationRepository.findByJob_JobIdAndUser_UserId(1L, 1L)).thenReturn(application);

        jobService.withdrawApplication(1L, 1L);

        assertEquals(ApplicationStatus.WITHDRAWN, application.getStatus());
        verify(applicationRepository, times(1)).save(application);
    }

    @Test
    public void withdrawApplication_ShouldThrowException_WhenApplicationNotFound() {
        when(applicationRepository.findByJob_JobIdAndUser_UserId(1L, 1L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            jobService.withdrawApplication(1L, 1L);
        });

        assertEquals("Application not found", exception.getMessage());
    }

    @Test
    public void getJobsByEmployerId_ShouldReturnJobs() {
        Job job1 = new Job();
        job1.setJobId(1L);
        job1.setJobTitle("Software Engineer");

        Job job2 = new Job();
        job2.setJobId(2L);
        job2.setJobTitle("Data Analyst");

        when(jobRepository.findByEmployer_EmpolyerId(1L)).thenReturn(Arrays.asList(job1, job2));

        List<Job> jobs = jobService.getJobsByEmployerId(1L);

        assertEquals(2, jobs.size());
        assertEquals("Software Engineer", jobs.get(0).getJobTitle());
        assertEquals("Data Analyst", jobs.get(1).getJobTitle());
    }

    @Test
    public void getJobsNotAppliedByUser_ShouldReturnJobs() {
        when(jobRepository.findJobsNotAppliedByUser(1L)).thenReturn(Collections.emptyList());

        List<Job> jobs = jobService.getJobsNotAppliedByUser(1L);

        assertTrue(jobs.isEmpty());
        verify(jobRepository, times(1)).findJobsNotAppliedByUser(1L);
    }
}
package varta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job job;

    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobService = new JobService();
    }

    @Test
    @DisplayName("should launch job successfully")
    void launchJob_launchesJobSuccessfully() throws Exception {
        when(job.getName()).thenReturn("testJob");
        when(jobLauncher.run(eq(job), any(JobParameters.class))).thenReturn(new JobExecution(1L));

        jobService.launchJob(job, jobLauncher);

        verify(jobLauncher).run(eq(job), any(JobParameters.class));
    }

    @Test
    @DisplayName("should include start time in job parameters")
    void launchJob_includesStartTimeInParameters() throws Exception {
        when(job.getName()).thenReturn("testJob");
        ArgumentCaptor<JobParameters> captor = ArgumentCaptor.forClass(JobParameters.class);
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(new JobExecution(1L));

        jobService.launchJob(job, jobLauncher);

        verify(jobLauncher).run(eq(job), captor.capture());
        JobParameters capturedParams = captor.getValue();
        assertThat(capturedParams.getLong("startTime")).isNotNull();
    }

    @Test
    @DisplayName("should handle exception during job launch")
    void launchJob_handlesException() throws Exception {
        when(job.getName()).thenReturn("testJob");
        doThrow(new JobExecutionAlreadyRunningException("Already running"))
                .when(jobLauncher).run(any(Job.class), any(JobParameters.class));

        // Should not throw exception
        jobService.launchJob(job, jobLauncher);

        verify(jobLauncher).run(eq(job), any(JobParameters.class));
    }
}


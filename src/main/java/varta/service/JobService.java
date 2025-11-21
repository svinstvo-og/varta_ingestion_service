package varta.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobService {

    public void launchJob(Job job, JobLauncher jobLauncher) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startTime", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);

            log.info("Batch job '{}' has been started.", job.getName());
        } catch (Exception e) {
            log.error("Error starting '{}' job {}", job.getName(), e.getMessage());
        }
    }
}

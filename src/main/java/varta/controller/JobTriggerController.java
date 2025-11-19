package varta.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/job/")
@Slf4j
public class JobTriggerController {

    private final JobLauncher jobLauncher;
    private final Job creditUserJob;

    public JobTriggerController(JobLauncher jobLauncher, Job creditUserJob) {
        this.jobLauncher = jobLauncher;
        this.creditUserJob = creditUserJob;
    }

    @PostMapping("start/credit-user")
    private void launchCreditUserJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startTime", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.    run(creditUserJob, jobParameters);

            log.info("Batch job 'creditUser' has been started.");
        } catch (Exception e) {
            log.error("Error starting 'creditUser' job {}", e.getMessage());
        }
    }
}

package varta.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import varta.service.JobService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/job/")
@Slf4j
public class JobTriggerController {

    private final JobLauncher jobLauncher;
    private final JobService jobService;

    private final Job creditUserJob;
    private final Job creditStoreJob;


    public JobTriggerController(JobLauncher jobLauncher,
                                List<String> jobNames,
                                JobService jobService,
                                @Qualifier("creditUserJob") Job creditUserJob,
                                @Qualifier("creditStoreJob") Job creditStoreJob) {
        this.jobLauncher = jobLauncher;
        this.creditUserJob = creditUserJob;
        this.creditStoreJob = creditStoreJob;
        this.jobService = jobService;
    }

    @PostMapping("start/credit_user")
    private void launchCreditUserJob() {
        log.info("Accepted launch credit user job");
        jobService.launchJob(creditUserJob, jobLauncher);
    }

    @PostMapping("start/credit_store")
    private void launchCreditStoreJob() {
        log.info("Accepted launch store user job");
        jobService.launchJob(creditStoreJob, jobLauncher);
    }
}

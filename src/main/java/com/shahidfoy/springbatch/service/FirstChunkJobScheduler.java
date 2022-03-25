package com.shahidfoy.springbatch.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// commenting out service will ignore the job
// @Service
public class FirstChunkJobScheduler {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("firstChunkJob")
    Job firstChunkJob;

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void jobStarter() {
        Map<String, JobParameter> params = new HashMap<>();
        params.put("currentTime", new JobParameter(System.currentTimeMillis()));

        JobParameters jobParameters = new JobParameters(params);

        try {
            JobExecution jobExecution = null;
            jobExecution = jobLauncher.run(firstChunkJob, jobParameters);
            System.out.println("jobExecution ID = " + jobExecution.getId());
        } catch (Exception e) {
            System.out.println("Exception while starting job");
        }
    }
}

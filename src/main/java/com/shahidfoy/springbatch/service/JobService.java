package com.shahidfoy.springbatch.service;

import com.shahidfoy.springbatch.request.JobParamsRequest;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobService {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("firstJob")
    Job firstJob;

    @Autowired
    @Qualifier("firstChunkJob")
    Job firstChunkJob;

    @Async
    public void startJob(String jobName, List<JobParamsRequest> jobParamsRequestList) {
        Map<String, JobParameter> params = new HashMap<>();
        params.put("currentTime", new JobParameter(System.currentTimeMillis()));

        jobParamsRequestList.forEach(jobParamsRequest -> {
            params.put(jobParamsRequest.getParamKey(), new JobParameter(jobParamsRequest.getParamValue()));
        });

        JobParameters jobParameters = new JobParameters(params);

        try {
            JobExecution jobExecution = null;
            if (jobName.equals("First Job")) {
                jobExecution = jobLauncher.run(firstJob, jobParameters);
            } else if (jobName.equals("First Chunk Job")) {
                jobExecution = jobLauncher.run(firstChunkJob, jobParameters);
            }
            assert jobExecution != null;
            System.out.println("jobExecution ID = " + jobExecution.getId());
        } catch (Exception e) {
            System.out.println("Exception while starting job");
        }

    }
}

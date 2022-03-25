package com.shahidfoy.springbatch.controller;

import com.shahidfoy.springbatch.request.JobParamsRequest;
import com.shahidfoy.springbatch.service.JobService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// commenting out controller will ignore the job
//@Service
//@RestController
//@RequestMapping("/api/job")
public class JobController {

    @Autowired
    JobService jobService;

    @Autowired
    JobOperator jobOperator;

    @GetMapping("/start/{jobName}")
    public String startJob(@PathVariable String jobName, @RequestBody List<JobParamsRequest> jobParamsRequestList) {

        jobService.startJob(jobName, jobParamsRequestList);

        return "Job Started...";
    }

    @GetMapping("/stop/{executionId")
    public String stopJob(@PathVariable long executionId) {
        try {
            jobOperator.stop(executionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Jop Stopped...";
    }
}

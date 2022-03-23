package com.shahidfoy.springbatch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstJobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Before job " + jobExecution.getJobInstance().getJobName());
        System.out.println("Job params " + jobExecution.getJobParameters());
        System.out.println("Job execution context " + jobExecution.getExecutionContext());

        jobExecution.getExecutionContext().put("jec", "the value");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("After job " + jobExecution.getJobInstance().getJobName());
        System.out.println("Job params " + jobExecution.getJobParameters());
        System.out.println("Job execution context " + jobExecution.getExecutionContext());
    }
}

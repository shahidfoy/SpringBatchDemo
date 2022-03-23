package com.shahidfoy.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableBatchProcessing
@EnableScheduling
@ComponentScan({"com.shahidfoy.springbatch.config",
		"com.shahidfoy.springbatch.service",
		"com.shahidfoy.springbatch.listener",
		"com.shahidfoy.springbatch.processor",
		"com.shahidfoy.springbatch.reader",
		"com.shahidfoy.springbatch.writer",
		"com.shahidfoy.springbatch.controller"})
public class SpringBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchApplication.class, args);
	}

}

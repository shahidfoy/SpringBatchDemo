package com.shahidfoy.springbatch.config;

import com.shahidfoy.springbatch.listener.FirstJobListener;
import com.shahidfoy.springbatch.listener.FirstStepListener;
import com.shahidfoy.springbatch.model.StudentCsv;
import com.shahidfoy.springbatch.processor.FirstItemProcessor;
import com.shahidfoy.springbatch.reader.FirstItemReader;
import com.shahidfoy.springbatch.service.SecondTasklet;
import com.shahidfoy.springbatch.writer.CsvItemWriter;
import com.shahidfoy.springbatch.writer.FirstItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Configuration
public class SampleJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SecondTasklet secondTasklet;

    @Autowired
    private FirstJobListener firstJobListener;

    @Autowired
    private FirstStepListener firstStepListener;

    @Autowired
    private FirstItemReader firstItemReader;

    @Autowired
    private FirstItemProcessor firstItemProcessor;

    @Autowired
    private FirstItemWriter firstItemWriter;

    @Autowired
    private CsvItemWriter csvItemWriter;

// commenting out bean will ignore the job
    // @Bean
    public Job firstJob() {
        return jobBuilderFactory.get("First Job")
                .incrementer(new RunIdIncrementer())
                .start(firstStep())
                .next(secondStep())
                .listener(firstJobListener)
                .build();
    }

    private Step firstStep() {
        return stepBuilderFactory.get("First Step")
                .tasklet(firstTask())
                .listener(firstStepListener)
                .build();
    }

    private Step secondStep() {
        return stepBuilderFactory.get("Second Step")
                .tasklet(this.secondTasklet)
//                .tasklet(secondTask())
                .build();
    }

    private Tasklet firstTask() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("This is first tasklet step");
                System.out.println(chunkContext.getStepContext().getJobExecutionContext());
                System.out.println("SEC == " + chunkContext.getStepContext().getStepExecutionContext());
                return RepeatStatus.FINISHED;
            }
        };
    }

    // chunk jobs

    // @Bean
    public Job firstChunkJob() {
        return jobBuilderFactory.get("First Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(firstChunkStep())
                .next(secondStep())
                .build();
    }

    private Step firstChunkStep() {
        return stepBuilderFactory.get("First Chunk Step")
                .<Integer, Long>chunk(3)
                .reader(firstItemReader)
                .processor(firstItemProcessor)
                .writer(firstItemWriter)
                .build();
    }

    // flat file csv item reader

    public FlatFileItemReader<StudentCsv> flatFileItemReader() {
        FlatFileItemReader<StudentCsv> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(
                new File(
                        "D:\\Documents\\Learning\\Spring\\Batch\\BatchProcessingWithSpringBatch\\spring-batch\\spring-batch\\InputFiles\\students.csv")));

        flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {{
                setLineTokenizer(new DelimitedLineTokenizer() {{
                    setNames("ID", "First Name", "Last Name", "Email");
                }});
                setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {{
                    setTargetType(StudentCsv.class);
                }});
            }});

        flatFileItemReader.setLinesToSkip(1);

        return flatFileItemReader;
    }

    @Bean
    public Job csvChunkJob() {
        return jobBuilderFactory.get("CSV Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(csvChunkStep())
                .next(secondStep())
                .build();
    }

    private Step csvChunkStep() {
        return stepBuilderFactory.get("CSV Chunk Step")
                .<StudentCsv, StudentCsv>chunk(3)
                .reader(flatFileItemReader())
                .writer(csvItemWriter)
                .build();
    }
}

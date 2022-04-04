package com.shahidfoy.springbatch.config;

import com.shahidfoy.springbatch.listener.FirstJobListener;
import com.shahidfoy.springbatch.listener.FirstStepListener;
import com.shahidfoy.springbatch.model.Student;
import com.shahidfoy.springbatch.model.StudentJdbc;
import com.shahidfoy.springbatch.model.StudentXml;
import com.shahidfoy.springbatch.processor.FirstItemProcessor;
import com.shahidfoy.springbatch.reader.FirstItemReader;
import com.shahidfoy.springbatch.service.SecondTasklet;
import com.shahidfoy.springbatch.writer.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
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

    @Autowired
    private JsonItemWriter jsonItemWriter;

    @Autowired
    private XmlItemWriter xmlItemWriter;

    @Autowired
    private JdbcItemWriter jdbcItemWriter;

    // used to connect to sql database as spring.datasource from application.properties
    @Bean
    @Primary
    // batch datasource
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource datasource() {
        return DataSourceBuilder.create().build();
    };

    @Bean
    // another datasource
    @ConfigurationProperties(prefix = "spring.universitydatasource")
    public DataSource universityDatasource() {
        return DataSourceBuilder.create().build();
    };


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

    public FlatFileItemReader<Student> flatFileItemReader() {
        FlatFileItemReader<Student> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(
                new File(
                        "D:\\Documents\\Learning\\Spring\\Batch\\BatchProcessingWithSpringBatch\\spring-batch\\spring-batch\\InputFiles\\students.csv")));

        flatFileItemReader.setLineMapper(new DefaultLineMapper<Student>() {{
                setLineTokenizer(new DelimitedLineTokenizer() {{
                    setNames("ID", "First Name", "Last Name", "Email");
                }});
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Student>() {{
                    setTargetType(Student.class);
                }});
            }});

        flatFileItemReader.setLinesToSkip(1);

        return flatFileItemReader;
    }

    // @Bean
    public Job csvChunkJob() {
        return jobBuilderFactory.get("CSV Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(csvChunkStep())
                .next(secondStep())
                .build();
    }

    private Step csvChunkStep() {
        return stepBuilderFactory.get("CSV Chunk Step")
                .<Student, Student>chunk(3)
                .reader(flatFileItemReader())
                .writer(csvItemWriter)
                .build();
    }

    // custom delimiter - sets custom delimiter this example uses | file

    public FlatFileItemReader<Student> flatFileCustomDelimiterItemReader() {
        FlatFileItemReader<Student> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(
                new File(
                        "D:\\Documents\\Learning\\Spring\\Batch\\BatchProcessingWithSpringBatch\\spring-batch\\spring-batch\\InputFiles\\students.txt")));

        flatFileItemReader.setLineMapper(new DefaultLineMapper<Student>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("ID", "First Name", "Last Name", "Email");
                // pipe file set here
                setDelimiter("|");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Student>() {{
                setTargetType(Student.class);
            }});
        }});

        flatFileItemReader.setLinesToSkip(1);

        return flatFileItemReader;
    }

    // @Bean
    public Job customDelimiterChunkJob() {
        return jobBuilderFactory.get("Delimiter Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(customDelimiterChunkStep())
                .next(secondStep())
                .build();
    }

    private Step customDelimiterChunkStep() {
        return stepBuilderFactory.get("Delimiter Chunk Step")
                .<Student, Student>chunk(3)
                .reader(flatFileCustomDelimiterItemReader())
                .writer(csvItemWriter)
                .build();
    }

    // pass file name through program arguments

    @Bean
    @StepScope
    public FlatFileItemReader<Student> flatFilePassFileNameItemReader(
            @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource
    ) {
        FlatFileItemReader<Student> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(fileSystemResource);

        flatFileItemReader.setLineMapper(new DefaultLineMapper<Student>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("ID", "First Name", "Last Name", "Email");
                // pipe file set here
                setDelimiter("|");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Student>() {{
                setTargetType(Student.class);
            }});
        }});

        flatFileItemReader.setLinesToSkip(1);

        return flatFileItemReader;
    }

    // turn this on if you have a program argument to run
    // example argument:  inputFile="D:\\Documents\\Learning\\Spring\\Batch\\BatchProcessingWithSpringBatch\\spring-batch\\spring-batch\\InputFiles\\students.txt"
    // @Bean
    public Job passFileNameChunkJob() {
        return jobBuilderFactory.get("Pass File Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(passFileNameChunkStep())
                .next(secondStep())
                .build();
    }

    private Step passFileNameChunkStep() {
        return stepBuilderFactory.get("Pass File Chunk Step")
                .<Student, Student>chunk(3)
                .reader(flatFilePassFileNameItemReader(null))
                .writer(csvItemWriter)
                .build();
    }

    // simple flat file item reader

    public FlatFileItemReader<Student> flatFileSimpleItemReader() {
        FlatFileItemReader<Student> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(
                new File(
                        "D:\\Documents\\Learning\\Spring\\Batch\\BatchProcessingWithSpringBatch\\spring-batch\\spring-batch\\InputFiles\\students.txt")));

        DefaultLineMapper<Student> defaultLineMapper = new DefaultLineMapper<Student>();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("ID", "First Name", "Last Name", "Email");
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<Student>();
        fieldSetMapper.setTargetType(Student.class);

        flatFileItemReader.setLinesToSkip(1);
        return flatFileItemReader;
    }

    // JSON item reader
    // example argument:  inputFile="D:\\Documents\\Learning\\Spring\\Batch\\BatchProcessingWithSpringBatch\\spring-batch\\spring-batch\\InputFiles\\students.json"
    @Bean
    @StepScope
    public JsonItemReader<Student> jsonItemReader(
            @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource
    ) {
        JsonItemReader<Student> jsonItemReader = new JsonItemReader<>();
        jsonItemReader.setResource(fileSystemResource);
        jsonItemReader.setJsonObjectReader(
                new JacksonJsonObjectReader<>(Student.class)
        );
        // reads a max eight items
        jsonItemReader.setMaxItemCount(8);
        // starts at item two
        jsonItemReader.setCurrentItemCount(2);
        return jsonItemReader;
    }

    // @Bean
    public Job jsonChunkJob() {
        return jobBuilderFactory.get("JSON Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(jsonChunkStep())
                .next(secondStep())
                .build();
    }

    private Step jsonChunkStep() {
        return stepBuilderFactory.get("JSON Chunk Step")
                .<Student, Student>chunk(3)
                .reader(jsonItemReader(null))
                .writer(jsonItemWriter)
                .build();
    }

    // XML item reader
    // example argument:  inputFile="D:\\Documents\\Learning\\Spring\\Batch\\BatchProcessingWithSpringBatch\\spring-batch\\spring-batch\\InputFiles\\students.xml"
    @Bean
    @StepScope
    public StaxEventItemReader<StudentXml> staxEventItemReader(
            @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource
    ) {
        StaxEventItemReader<StudentXml> staxEventItemReader =  new StaxEventItemReader<>();
        staxEventItemReader.setResource(fileSystemResource);
        staxEventItemReader.setFragmentRootElementName("student");
        staxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
            {
                setClassesToBeBound(StudentXml.class);
            }
        });
        return staxEventItemReader;
    }

    // @Bean
    public Job xmlChunkJob() {
        return jobBuilderFactory.get("XML Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(xmlChunkStep())
                .next(secondStep())
                .build();
    }

    private Step xmlChunkStep() {
        return stepBuilderFactory.get("XML Chunk Step")
                .<StudentXml, StudentXml>chunk(3)
                .reader(staxEventItemReader(null))
                .writer(xmlItemWriter)
                .build();
    }

    // JDBC item reader

    public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
        JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader = new JdbcCursorItemReader<>();
        // connects to different datasource
        jdbcCursorItemReader.setDataSource(this.universityDatasource());
        jdbcCursorItemReader.setSql(
                "select id, first_name as firstName, last_name as lastName, email from student"
        );
        jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>() {
            {
                setMappedClass(StudentJdbc.class);
            }
        });
        return jdbcCursorItemReader;
    }

    @Bean
    public Job jdbcChunkJob() {
        return jobBuilderFactory.get("JDBC Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(jdbcChunkStep())
                .next(secondStep())
                .build();
    }

    private Step jdbcChunkStep() {
        return stepBuilderFactory.get("JDBC Chunk Step")
                .<StudentJdbc, StudentJdbc>chunk(3)
                .reader(jdbcCursorItemReader())
                .writer(jdbcItemWriter)
                .build();
    }
}

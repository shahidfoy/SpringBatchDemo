package com.shahidfoy.springbatch.writer;

import com.shahidfoy.springbatch.model.StudentJdbc;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JdbcItemWriter implements ItemWriter<StudentJdbc> {
    @Override
    public void write(List<? extends StudentJdbc> list) throws Exception {
        System.out.println("Inside JSON item writer");
        list.forEach(System.out::println);
    }
}

package com.shahidfoy.springbatch.writer;

import com.shahidfoy.springbatch.model.Student;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonItemWriter implements ItemWriter<Student> {
    @Override
    public void write(List<? extends Student> list) throws Exception {
        System.out.println("Inside JSON item writer");
        list.forEach(System.out::println);
    }
}

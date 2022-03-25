package com.shahidfoy.springbatch.writer;

import com.shahidfoy.springbatch.model.StudentCsv;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CsvItemWriter implements ItemWriter<StudentCsv> {
    @Override
    public void write(List<? extends StudentCsv> list) throws Exception {
        System.out.println("Inside csv item writer");
        list.forEach(System.out::println);
    }
}

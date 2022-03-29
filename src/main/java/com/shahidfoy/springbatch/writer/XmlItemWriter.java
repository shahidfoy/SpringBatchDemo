package com.shahidfoy.springbatch.writer;

import com.shahidfoy.springbatch.model.StudentXml;
import org.springframework.stereotype.Component;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Component
public class XmlItemWriter implements ItemWriter<StudentXml> {
    @Override
    public void write(List<? extends StudentXml> list) throws Exception {
        System.out.println("Inside XML item writer");
        list.forEach(System.out::println);
    }
}

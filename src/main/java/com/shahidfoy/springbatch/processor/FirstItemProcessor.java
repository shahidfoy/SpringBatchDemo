package com.shahidfoy.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FirstItemProcessor implements ItemProcessor<Integer, Long> {


    @Override
    public Long process(Integer integer) throws Exception {
        System.out.println("Inside item processor");
        return (long) (integer + 20);
    }
}

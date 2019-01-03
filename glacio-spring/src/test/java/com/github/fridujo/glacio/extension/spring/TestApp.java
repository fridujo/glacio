package com.github.fridujo.glacio.extension.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestApp {

    @Bean
    BeanA beanA() {
        return new BeanA("test");
    }

    @Bean
    BeanB beanB() {
        return new BeanB(42);
    }
}

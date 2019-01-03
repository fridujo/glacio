package com.github.fridujo.glacio.extension.spring;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.github.fridujo.glacio.running.api.Then;
import com.github.fridujo.glacio.running.api.When;

public class StepDefForA {

    private final BeanA beanA;
    private String aValue;

    StepDefForA(@Autowired BeanA beanA, @Value("${my.prop}") double myProp, ApplicationContext applicationContext) {
        this.beanA = beanA;
        assertThat(myProp).isEqualTo(45.3D);
        assertThat(applicationContext).isNotNull();
    }

    @When("checking value of A")
    public void checking_value_of_a() {
        this.aValue = beanA.getA();
    }

    @Then("value of A is '(.*)'")
    public void value_of_a_is(String expected) {
        assertThat(aValue).isEqualTo(expected);
    }
}

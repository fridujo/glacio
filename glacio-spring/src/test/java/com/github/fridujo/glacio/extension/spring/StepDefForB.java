package com.github.fridujo.glacio.extension.spring;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.fridujo.glacio.running.api.Then;
import com.github.fridujo.glacio.running.api.When;

public class StepDefForB {

    private final BeanB beanB;
    private int bValue;

    @Autowired
    StepDefForB(BeanB beanB) {
        this.beanB = beanB;
    }

    @When("checking value of B")
    public void checking_value_of_b() {
        this.bValue = beanB.getB();
    }

    @Then("value of B is '(.*)'")
    public void value_of_b_is(int expected) {
        assertThat(bValue).isEqualTo(expected);
    }
}

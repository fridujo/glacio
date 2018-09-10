package com.github.fridujo.glacio.junit.sample;

import com.github.fridujo.glacio.running.api.Given;
import com.github.fridujo.glacio.running.api.Then;
import com.github.fridujo.glacio.running.api.When;

import static org.assertj.core.api.Assertions.assertThat;

public class ColorStepDefs {

    private String color;
    private String testedColor;

    @Given("^the color (.+)$")
    public void the_color(String color) {
        this.color = color;
    }

    @When("^this color is being tested$")
    public void this_color_is_being_tested() {
        this.testedColor = color;
    }

    @Then("^the tested color is (.+)$")
    public void the_tested_color_is(String expected) {
        assertThat(testedColor).isEqualTo(expected);
    }
}

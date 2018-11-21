package com.github.fridujo.glacio.sample;

import org.assertj.core.api.Assertions;

import com.github.fridujo.glacio.model.DocString;
import com.github.fridujo.glacio.running.api.Given;
import com.github.fridujo.glacio.running.api.Then;
import com.github.fridujo.glacio.running.api.When;

public class TestStepDef {

    private String name;
    private boolean clicked;
    private String document;
    private String documentType;

    @Given("a user named (.*)")
    public void a_user_with_name(String name) {
        this.name = name;
    }

    @Given("a user with data (.*)")
    public void a_user_with_data(String data) {
        // Do nothing
    }

    @Given("a user with (.*)")
    public void a_user_with(String parameter) {
        // Do nothing
    }

    @Given("a document")
    public void a_document(DocString docString) {
        document = docString.getContent();
        documentType = docString.getContentType().get();
    }

    @When("the user clicks on the button")
    public void the_user_clicks_on_the_button() {
        clicked = true;
    }

    @When("package protected method")
    void package_protected_method() {
        // Do nothing
    }

    @Then("the button have been clicked on")
    public void the_button_have_been_clicked_on() {
        Assertions.assertThat(clicked).isTrue();
    }

    @Then("the user name is '(.*)'")
    public void the_user_name_is(String expectedName) {
        Assertions.assertThat(name).isEqualTo(expectedName);
    }

    public String getName() {
        return name;
    }

    public boolean isClicked() {
        return clicked;
    }

    public String getDocument() {
        return document;
    }

    public String getDocumentType() {
        return documentType;
    }
}

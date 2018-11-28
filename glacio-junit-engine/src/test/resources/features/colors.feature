Feature: testing colors
    Background:
        Given the color purple

    Scenario: red
        Given some color
            the color blue
            the color red
        When this color is being tested
        Then the tested color is red

    Scenario Outline: other colors
        Given the color <color>
        When this color is being tested
        Then the tested color is <color>

        Examples:
        | color |
        | blue  |
        | green |

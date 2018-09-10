Feature: testing colors
    Background:
        Given the color purple

    Scenario: red
        Given some color
            the color blue
            the color green
        When this color is being tested
        Then the tested color is red

    Scenario Outline: other colors
        Given the color <color>
        When this color is being tested
        Then the tested color is yellow

        Examples:
        | color |
        | blue  |
        | green |

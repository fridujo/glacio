Feature: Validating Spring extension

    Scenario: parameter-based autowiring
        When checking value of A
        Then value of A is 'test'

    Scenario: constructor-based autowiring
        When checking value of B
        Then value of B is '42'

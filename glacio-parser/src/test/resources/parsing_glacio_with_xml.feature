# language: en

Feature: Could parse scenarios with xml data

    Scenario: Simple scenario with XML data in docstring
        When Parse step with XML data in docstring
        """ application/xml
        <book><title>miam</title><author>chef</author></book>
        """
        Then The parsing does not try to look for parameters

    Scenario Outline: Examples with XML data in docstring
        When parsing a step with XML data in docString
        """ <contentType>
        <book><title><titleVar></title><author>chef</author></book>
        """
        Then the <counter> parsing succeed
    Examples:
      | contentType     | titleVar | counter |
      | application/xml | miam     | first   |
      | xml             | yummy    | second  |

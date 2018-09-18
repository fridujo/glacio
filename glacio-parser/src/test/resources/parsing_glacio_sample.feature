# language: en

# arbitrary
# comments

@tag1
@tag2
Feature: User login

    Free form description
    can be placed here

    Background:
        Given a test setup

        # This comment will be ignored
        And another one
           with a substep

    Scenario: Successful login

        Free form description
        can be placed here

        Given a user with valid credentials
            Insert generated user with valid credentials in database
                Generate variable 'username'
                Generate variable 'password'
                Execute SQL
                """ sql
                  INSERT INTO USERS(USERNAME, PASSWORD, ENABLED)
                    VALUES('${username}', '${password}', 1)
                """
        And the user in on the login page
            Open a 'Chrome' browser
            Navigate to
            | protocol | address         | path   |
            | https    | www.yourapp.com | /login |
            Click on the 'Sign In' button
        When the user signs in
            Fill 'username' input with '${username}'
            Fill 'password' input with '${password}'
            Click on the 'Sign In' button
        Then the user is logged in
            Relative URL is '/home'

@tag3 @tag4
    Scenario Outline: Successful login using template

        Given a user with valid credentials
        And the user is on the login page with a <browser_type> browser
        When the user signs in
        Then the user is logged in

        Examples:
          | browser_type |
          | Firefox      |
          | Chrome       |

# Glacio
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c0142d637ee44a86a201c5caba95a8d9)](https://www.codacy.com/app/ledoyen/glacio?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fridujo/glacio&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.com/fridujo/glacio.svg?branch=master)](https://travis-ci.com/fridujo/glacio)
[![codecov](https://codecov.io/gh/fridujo/glacio/branch/master/graph/badge.svg)](https://codecov.io/gh/fridujo/glacio)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.fridujo/glacio.svg)](https://search.maven.org/#search|ga|1|a:"glacio")
[![License](https://img.shields.io/github/license/fridujo/spring-automocker.svg)](https://opensource.org/licenses/Apache-2.0)

Multi-level [Gherkin](https://docs.cucumber.io/gherkin/reference/)-like language.

# What it is

Using [Cucumber](https://docs.cucumber.io/), you would write something like the following and let developers translate it however they want:
```gherkin
Feature: User login

    Scenario: Successful login

        Given a user on the login page
        When the user signs in with valid credentials
        Then the user is logged in
```

Except several concepts in this spec are subject to interpretation.
 * what browser is the user using to get to the login page? (firefox, chrome, etc.)
 * are we even executing this test on a fully deployed application or in the context of an integration test? (selenium, mocked application, etc.)
 * what does having valid credentials mean? (inserted into the database on the fly, created through the registration process, using an already existing account, etc.)
 * is real-user sign in required or is an HTTP post on the back API sufficient? (this leads also to test strategy, whether all tests should use a tool like selenium, or API calls are sufficient for most of them)
 * how do we know that the user is logged in? (check the url is `/home`, check the title of the page, check the presence of a cookie, etc.)

Using Glacio, you have room to answer these uncertainties together with all actors:
```glacio
Feature: User login

    Scenario: Successful login

        Given a user with valid credentials
            Insert generated user with valid credentials in database
                Generate variable 'username'
                Generate variable 'password'
                Execute SQL
                """
                  INSERT INTO USERS(USERNAME, PASSWORD, ENABLED)
                    VALUES('${username}', '${password}', 1)
                """
        And the user in on the login page
            Open a 'Chrome' browser
            Navigate to 'https://www.yourapp.com'
            Click on the 'Sign In' button
        When the user signs in
            Fill 'username' input with '${username}'
            Fill 'password' input with '${password}'
            Click on the 'Sign In' button
        Then the user is logged in
            Relative URL is '/home'
```

In the end, like with [Cucumber](https://docs.cucumber.io/), developers will have to code some glue to make leaf-steps runnable.  
You can choose how many levels of explanation you have for each step.  
Go deep enough though and the code base will only contain generic little Lego pieces you can re-use to create new tests that are instantly runnable.

# Where it starts
There is a common problem encountered using BDD tools like [Cucumber](https://docs.cucumber.io/).  
This is:
 * non-technical actors write specs
 * developers write glue code containing hidden business specificities

That is an historic clivage which tends to leave grey areas undiscovered until implementation time.  
Furthermore, non-technical actors can point out relevant aspects of implementation just as developers can do the same about the business part (aka specs).

As Seb Rose stated in [this article](http://claysnow.co.uk/the-testing-iceberg/), levels of readability can differ from the ones encountered in usual test segregation (unit, integration, acceptance, etc.).

# Motivation
This project is intended to solve this issue by supplying a shared medium for all actors to contribute together.  

The language is inspired by [Gherkin](https://docs.cucumber.io/gherkin/reference/) as it has many qualities (expressiveness, i18n, support of many programming languages).  
[Substeps framework](http://substeps.github.io/introduction/overview/) is also a source of inspiration, but the goal is to keep the Glacio language expressive by avoiding the need to switch between files to have the complete picture.

# Vocabulary

You may want to gather your domain vocabulary in one place, to avoid duplication and ambiguous definition (same step labels in different scenarios but with different meanings).

For this specific use case, like with [Substeps](http://substeps.github.io/introduction/overview/), you can write definitions in `.vocabulary` files.

**user_login.feature**
```glacio
Feature: User login

    Scenario Outline: Successful login

        Given a user with valid credentials
        And the user is on the login page with a <browser_type> browser
        When the user signs in
        Then the user is logged in

        Examples:
          | browser_type |
          | Firefox      |
          | Chrome       |
```

**application.vocabulary**
```markdown
Insert generated user with valid credentials in database
    * Generate variable 'username'
    * Generate variable 'password'
    * Execute SQL
      ```sql
      INSERT INTO USERS(USERNAME, PASSWORD, ENABLED)
        VALUES('${username}', '${password}', 1)
      ```

a user with valid credentials
    * Insert generated user with valid credentials in database

the user is on the login page with a <browser_type> browser
    * Open a '<browser_type>' browser
    * Navigate to 'https://www.yourapp.com'
    * Click on the 'Sign In' button

the user signs in
    * Fill 'username' input with '${username}'
    * Fill 'password' input with '${password}'
    * Click on the 'Sign In' button

the user is logged in
    * Relative URL is '/home'
```


## Getting Started

### Maven
Add the following dependency to your **pom.xml**
```xml
<dependency>
    <groupId>com.github.fridujo</groupId>
    <artifactId>glacio-junit-engine</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

### Gradle
Add the following dependency to your **build.gradle**
```groovy
repositories {
	mavenCentral()
}

// ...

dependencies {
	// ...
	testCompile('com.github.fridujo:glacio-junit-engine:1.0.0-SNAPSHOT')
	// ...
}
```

### Building from Source

You need [JDK-8](http://jdk.java.net/8/) to build Glacio. The project can be built with Maven using the following command.
```
mvn clean package
```

Tests are split in:

* **unit tests** covering features and borderline cases: `mvn test`
* **mutation tests**, to help understand what is missing in test assertions: `mvn org.pitest:pitest-maven:mutationCoverage`


### Installing in the Local Maven Repository

The project can be installed in a local Maven Repository for usage in other projects via the following command.
```
mvn clean install
```

### Using the latest SNAPSHOT

The master of the project pushes SNAPSHOTs in Sonatype's repo.

To use the latest master build add Sonatype OSS snapshot repository, for Maven:
```
<repositories>
    ...
    <repository>
        <id>sonatype-oss-spanshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>
```

For Gradle:
```groovy
repositories {
    // ...
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots"
    }
}

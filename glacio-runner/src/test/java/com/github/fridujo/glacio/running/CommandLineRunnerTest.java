package com.github.fridujo.glacio.running;

import org.junit.jupiter.api.Test;

class CommandLineRunnerTest {

    @Test
    void nominal() {
        CommandLineRunner.main(new String[]{"-g", "com.github.fridujo.glacio.running.stepdefs", "classpath:features"});
    }
}

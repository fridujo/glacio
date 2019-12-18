package com.github.fridujo.glacio.running.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.github.fridujo.junit.extension.classpath.ModifiedClasspath;

class LoggerFactoryTest {

    @Test
    void slf4j_is_used_if_found() {
        Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);

        assertThat(logger).isExactlyInstanceOf(Slf4jLogger.class);
    }

    @Test
    @ModifiedClasspath(excludeJars = "slf4j-api")
    void jul_logger_is_the_default() {
        Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);

        assertThat(logger).isExactlyInstanceOf(JulLogger.class);
    }
}

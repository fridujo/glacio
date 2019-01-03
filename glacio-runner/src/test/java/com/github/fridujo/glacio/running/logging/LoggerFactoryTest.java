package com.github.fridujo.glacio.running.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.jupiter.api.Test;

class LoggerFactoryTest {

    @Test
    void jul_logger_is_the_default() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        ClassLoader classLoader = spy(new URLClassLoader(new URL[0], this.getClass().getClassLoader()));
        doThrow(new ClassNotFoundException()).when(classLoader).loadClass(eq("org.slf4j.Logger"));

        Field classLoaderField = LoggerFactory.class.getDeclaredField("CLASS_LOADER");

        // ignore private
        classLoaderField.setAccessible(true);
        // ignore final
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(classLoaderField, classLoaderField.getModifiers() & ~Modifier.FINAL);
        // replace classLoader by the mock
        classLoaderField.set(null, classLoader);

        Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);

        assertThat(logger).isExactlyInstanceOf(JulLogger.class);

        logger.info("test");
        logger.warn("test", new RuntimeException("test"));
    }

    @Test
    void slf4j_is_used_if_found() {
        Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);

        assertThat(logger).isExactlyInstanceOf(Slf4jLogger.class);

        logger.info("test");
        logger.warn("test", new RuntimeException("test"));
    }
}

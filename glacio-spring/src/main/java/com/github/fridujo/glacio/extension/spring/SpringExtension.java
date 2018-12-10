package com.github.fridujo.glacio.extension.spring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContextManager;
import org.springframework.util.Assert;

import com.github.fridujo.glacio.running.api.extension.AfterAllCallback;
import com.github.fridujo.glacio.running.api.extension.BeforeAllCallback;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.api.extension.ParameterContext;
import com.github.fridujo.glacio.running.api.extension.ParameterResolutionException;
import com.github.fridujo.glacio.running.api.extension.ParameterResolver;
import com.github.fridujo.glacio.running.api.extension.Store;

public class SpringExtension implements
    BeforeAllCallback,
    AfterAllCallback,
    ParameterResolver {

    private static final String NAMESPACE = "spring";

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getTestContextManager(context).beforeTestClass();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        try {
            getTestContextManager(context).afterTestClass();
        } finally {
            getStore(context).remove(context.getConfigurationClass());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        int index = parameterContext.getIndex();
        Executable executable = parameter.getDeclaringExecutable();
        return (executable instanceof Constructor &&
            AnnotatedElementUtils.hasAnnotation(executable, Autowired.class)) ||
            ParameterAutowireUtils.isAutowirable(parameter, index);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        int index = parameterContext.getIndex();
        Class<?> testClass = extensionContext.getConfigurationClass();
        ApplicationContext applicationContext = getTestContextManager(extensionContext).getTestContext().getApplicationContext();
        return ParameterAutowireUtils.resolveDependency(parameter, index, testClass, applicationContext);
    }

    private TestContextManager getTestContextManager(ExtensionContext context) {
        Assert.notNull(context, "ExtensionContext must not be null");
        Class<?> testClass = context.getConfigurationClass();
        Store store = getStore(context);
        return store.getOrComputeIfAbsent(testClass, TestContextManager::new, TestContextManager.class);
    }

    private Store getStore(ExtensionContext context) {
        return context.getStore(NAMESPACE);
    }


}

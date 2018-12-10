package com.github.fridujo.glacio.running.api.extension;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

public interface ParameterContext {

    Executable getDeclaringExecutable();
    
    Parameter getParameter();

    int getIndex();
}

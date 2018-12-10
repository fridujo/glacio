package com.github.fridujo.glacio.running.api.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented

@Repeatable(GlacioExtensions.class)
public @interface ExtendGlacioWith {

    Class<? extends Extension> value();
}

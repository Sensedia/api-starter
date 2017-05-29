package com.sensedia.api.infra;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Singleton {

}
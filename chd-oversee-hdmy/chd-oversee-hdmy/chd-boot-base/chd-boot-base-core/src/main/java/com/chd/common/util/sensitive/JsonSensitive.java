package com.chd.common.util.sensitive;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@Target(ElementType.FIELD)
public @interface JsonSensitive {
    SensitiveType value() default SensitiveType.Mobile;
}

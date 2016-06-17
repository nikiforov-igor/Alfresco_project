package ru.it.lecm.documents.utils;

import java.lang.annotation.*;

/**
 * User: dbashmakov
 * Date: 16.06.2016
 * Time: 15:30
 */

/**
 * Указывает, что данный метод должен быть зарегистрирован в качестве встроенной
 * функции SpEL.
 * functionName - имя функции. Необязательный параметр.
 *
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@interface SpELTemplateFunction {
    String functionName() default "";
}

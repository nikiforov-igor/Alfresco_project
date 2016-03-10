package ru.it.lecm.notifications.template;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает, что данный метод должен быть зарегистрирован в качестве встроенной
 * функции SpEL.
 * functionName - имя функции. Необязательный параметр.
 *
 * @author vlevin
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@interface NotificationsTemplateFunction {

	String functionName() default "";
}

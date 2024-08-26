package uz.ieltszone.zonelifeservice.aop;

import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {
    String[] roles();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
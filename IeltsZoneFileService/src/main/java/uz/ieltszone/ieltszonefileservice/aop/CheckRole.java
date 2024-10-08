package uz.ieltszone.ieltszonefileservice.aop;

import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {
    String[] roles();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
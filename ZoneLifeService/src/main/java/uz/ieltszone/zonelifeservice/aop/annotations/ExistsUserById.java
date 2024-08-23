package uz.ieltszone.zonelifeservice.aop.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uz.ieltszone.zonelifeservice.aop.validations.ExistsUserValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsUserValidator.class)
public @interface ExistsUserById {
    String message() default "User not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
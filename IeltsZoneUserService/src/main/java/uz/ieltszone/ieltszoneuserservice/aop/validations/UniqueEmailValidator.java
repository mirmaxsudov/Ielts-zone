package uz.ieltszone.ieltszoneuserservice.aop.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.ieltszone.ieltszoneuserservice.aop.annotations.UniqueEmailChecker;
import uz.ieltszone.ieltszoneuserservice.service.base.UserService;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmailChecker, String> {
    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null || email.isBlank())
            return true;

        return !userService.existsByEmail(email);
    }
}

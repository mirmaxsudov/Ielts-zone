package uz.ieltszone.ieltszoneuserservice.aop.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import uz.ieltszone.ieltszoneuserservice.aop.annotations.UniqueEmailChecker;
import uz.ieltszone.ieltszoneuserservice.repository.UserRepository;

@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmailChecker, String> {

    private final UserRepository userRepository;

    @Override
    public void initialize(UniqueEmailChecker constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank())
            return true;

        boolean b = userRepository.existsByEmail(email);

        System.out.println("b = " + b);
        
        return !b;
    }
}
package uz.ieltszone.zonelifeservice.aop.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.ieltszone.zonelifeservice.aop.annotations.ExistsUserById;
import uz.ieltszone.zonelifeservice.service.feign.UserFeign;

@Component
@RequiredArgsConstructor
public class ExistsUserValidator implements ConstraintValidator<ExistsUserById, Long> {
    private final UserFeign userFeign;

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext constraintValidatorContext) {
        return userFeign.existsById(userId);
    }
}

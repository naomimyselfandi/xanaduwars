package io.github.naomimyselfandi.xanaduwars.gameplay;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/// The annotated tile type must have a foundation.
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Structure.Validator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE_USE})
public @interface Structure {

    class Validator implements ConstraintValidator<Structure, TileType> {

        @Override
        public boolean isValid(TileType value, ConstraintValidatorContext context) {
            return value.foundation().isPresent();
        }

    }

    @SuppressWarnings("unused")
    String message() default "should be a structure";

    @SuppressWarnings("unused")
    Class<?>[] groups() default { };

    @SuppressWarnings("unused")
    Class<? extends Payload>[] payload() default { };

}

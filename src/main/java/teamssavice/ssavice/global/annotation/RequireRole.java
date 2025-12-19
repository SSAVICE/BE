package teamssavice.ssavice.global.annotation;

import teamssavice.ssavice.auth.constants.Role;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    Role value();
}
package teamssavice.ssavice.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ForbiddenException;

public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler
    ) throws Exception {
        if(!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole requireRole = Optional.ofNullable(handlerMethod.getMethodAnnotation(RequireRole.class))
                .orElse(handlerMethod.getBeanType().getAnnotation(RequireRole.class));
        if(requireRole == null) return true;

        Role userRole = Role.valueOf((String) request.getAttribute("role"));

        boolean hasAccess = Arrays.stream(requireRole.value())
                .anyMatch(required -> required.canAccess(userRole));

        if (!hasAccess) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        return true;
    }
}

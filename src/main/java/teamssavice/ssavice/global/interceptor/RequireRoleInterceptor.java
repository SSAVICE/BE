package teamssavice.ssavice.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ForbiddenException;

import java.util.Optional;

public class RequireRoleInterceptor implements HandlerInterceptor {

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

        Role required = requireRole.value();
        Role role = (Role) request.getAttribute("role");
        if (!role.canAccess(required)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        return true;
    }
}

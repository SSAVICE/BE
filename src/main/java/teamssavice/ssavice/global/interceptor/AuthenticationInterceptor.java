package teamssavice.ssavice.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.PermitAll;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;

public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler
    ) throws Exception {
        if(!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        PermitAll permitAll = handlerMethod.getMethodAnnotation(PermitAll.class);

        Role role = (Role) request.getAttribute("role");

        if (permitAll == null && role == null) {
            throw new AuthenticationException(ErrorCode.MISSING_TOKEN);
        }
        return true;
    }
}

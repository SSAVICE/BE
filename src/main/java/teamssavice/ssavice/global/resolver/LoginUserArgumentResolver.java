package teamssavice.ssavice.global.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.Authenticate;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;

public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticate.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String userId = (String) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        if(userId == null) {
            throw new AuthenticationException(ErrorCode.MISSING_TOKEN);
        }
        if(!role.equals(Role.USER.name())) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }

        return Long.parseLong(userId);
    }
}

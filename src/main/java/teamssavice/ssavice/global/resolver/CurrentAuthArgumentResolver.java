package teamssavice.ssavice.global.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentAuth;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.dto.Auth;
import teamssavice.ssavice.global.exception.AuthenticationException;

public class CurrentAuthArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentAuth.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String sub = (String) request.getAttribute("sub");
        String role = (String) request.getAttribute("role");

        if(sub == null) {
            throw new AuthenticationException(ErrorCode.MISSING_TOKEN);
        }

        return new Auth(Long.parseLong(sub), Role.valueOf(role));
    }
}

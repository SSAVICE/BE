package teamssavice.ssavice.global.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;

public class CurrentIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentId.class);
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

        if(sub == null) {
            throw new AuthenticationException(ErrorCode.MISSING_TOKEN);
        }

        return Long.parseLong(sub);
    }
}

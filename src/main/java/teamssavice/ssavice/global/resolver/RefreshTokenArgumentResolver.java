package teamssavice.ssavice.global.resolver;


import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import teamssavice.ssavice.global.annotation.CurrentRefreshToken;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;

public class RefreshTokenArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String HEADER_X_REFRESH_TOKEN = "X-Refresh-Token";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentRefreshToken.class)
                && parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory
    ) throws Exception {
        String xRefreshTokenHeader = webRequest.getHeader(HEADER_X_REFRESH_TOKEN);
        String refreshToken = resolveToken(xRefreshTokenHeader);

        if (refreshToken.isBlank()) {
            throw new AuthenticationException(ErrorCode.MISSING_TOKEN);
        }
        return refreshToken;
    }

    private String resolveToken(String xRefreshTokenHeader) {
        if(xRefreshTokenHeader == null || !xRefreshTokenHeader.startsWith(TOKEN_PREFIX)) {
            throw new AuthenticationException(ErrorCode.MISSING_TOKEN);
        }
        return xRefreshTokenHeader.substring(TOKEN_PREFIX.length());
    }
}

package teamssavice.ssavice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import teamssavice.ssavice.global.interceptor.AuthenticationInterceptor;
import teamssavice.ssavice.global.interceptor.AuthorizationInterceptor;
import teamssavice.ssavice.global.resolver.CurrentAuthArgumentResolver;
import teamssavice.ssavice.global.resolver.RefreshTokenArgumentResolver;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

    @Bean
    public AuthorizationInterceptor authorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Bean
    public CurrentAuthArgumentResolver currentAuthArgumentResolver() {
        return new CurrentAuthArgumentResolver();
    }

    @Bean
    public RefreshTokenArgumentResolver refreshTokenArgumentResolver() {
        return new RefreshTokenArgumentResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/api/**")
                .order(1);

        registry.addInterceptor(authorizationInterceptor())
                .addPathPatterns("/api/**")
                .order(2);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentAuthArgumentResolver());
        resolvers.add(refreshTokenArgumentResolver());
    }
}

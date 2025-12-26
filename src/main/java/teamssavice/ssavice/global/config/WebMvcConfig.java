package teamssavice.ssavice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import teamssavice.ssavice.global.interceptor.RequireRoleInterceptor;
import teamssavice.ssavice.global.resolver.CurrentIdArgumentResolver;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public RequireRoleInterceptor requireRoleInterceptor() {
        return new RequireRoleInterceptor();
    }

    @Bean
    public CurrentIdArgumentResolver currentIdArgumentResolver() {
        return new CurrentIdArgumentResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requireRoleInterceptor())
                .addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentIdArgumentResolver());
    }
}

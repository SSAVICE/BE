package teamssavice.ssavice.global.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import teamssavice.ssavice.auth.TokenProvider;
import teamssavice.ssavice.global.filter.JwtAuthFilter;

@Configuration
public class FilterConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter(TokenProvider tokenProvider) {
        return new JwtAuthFilter(tokenProvider);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);

        return registration;
    }
}

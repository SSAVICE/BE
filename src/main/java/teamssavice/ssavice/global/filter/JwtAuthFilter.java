package teamssavice.ssavice.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.filter.OncePerRequestFilter;
import teamssavice.ssavice.auth.TokenProvider;
import teamssavice.ssavice.global.exception.CustomException;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String accessToken = resolveToken(request);
        try {
            if(accessToken != null) {
                Claims claims = tokenProvider.getClaim(accessToken);
                request.setAttribute("sub", claims.getSubject());
                request.setAttribute("role", claims.get("role", String.class));
            }

            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            writeErrorResponse(response, e);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return null;
        }

        return authHeader.substring(TOKEN_PREFIX.length());
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            CustomException e
    ) throws IOException {

        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getErrorCode().getStatus());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("error_code", e.getErrorCode().getCode());

        response.setStatus(problemDetail.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), problemDetail);
    }
}

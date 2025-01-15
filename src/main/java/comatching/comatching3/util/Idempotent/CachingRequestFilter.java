package comatching.comatching3.util.Idempotent;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;


@Component
@Order(Integer.MIN_VALUE)
public class CachingRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // GET 요청이 아닌 경우에만 ContentCachingRequestWrapper 적용
        if (!"GET".equalsIgnoreCase(httpServletRequest.getMethod())) {
            // ContentCachingRequestWrapper로 요청 래핑
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpServletRequest);

            filterChain.doFilter(wrappedRequest, response);
        }
        // GET 요청인 경우 ContentCachingRequestWrapper 적용하지 않음
        else {
            filterChain.doFilter(request, response);
        }
    }
}

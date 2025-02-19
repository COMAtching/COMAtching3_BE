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

    /**
     *
     * POST 요청시에만 ContentCachingRequestWrapper에 request 저장
     *
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (!"GET".equalsIgnoreCase(httpServletRequest.getMethod())) {

            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpServletRequest);

            filterChain.doFilter(wrappedRequest, response);
        }

        else {
            filterChain.doFilter(request, response);
        }
    }
}

package comatching.comatching3.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

// todo : wss로 변경시 수정 필요할듯
@Component
public class CspFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " + // CDN을 허용하도록 추가
                        "style-src 'self' 'unsafe-inline';");
        chain.doFilter(req, res);
    }
}

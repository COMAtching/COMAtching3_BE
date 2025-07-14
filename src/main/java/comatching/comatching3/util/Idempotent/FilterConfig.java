package comatching.comatching3.util.Idempotent;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<CachingRequestFilter> loggingFilter() {
		FilterRegistrationBean<CachingRequestFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new CachingRequestFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(0);
		return registrationBean;
	}
}

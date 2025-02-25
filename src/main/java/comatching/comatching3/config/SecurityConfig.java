package comatching.comatching3.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import comatching.comatching3.auth.filter.AdminAuthenticationFilter;
import comatching.comatching3.auth.filter.RequestLoggingFilter;
import comatching.comatching3.auth.filter.UserAuthenticationFilter;
import comatching.comatching3.auth.service.CustomDetailsService;
import comatching.comatching3.auth.oauth2.handler.OAuth2FailureHandler;
import comatching.comatching3.auth.oauth2.handler.OAuth2SuccessHandler;
import comatching.comatching3.auth.oauth2.service.CustomOAuth2UserService;
import comatching.comatching3.users.service.BlackListService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final List<String> CORS_WHITELIST = List.of(
		"http://localhost:5173",
		"http://127.0.0.1:5500"
	);
	private static final List<String> WHITELIST = List.of(
		"/login", "/admin/**", "/charge-monitor/**", "/app/**",
		"/api/participations", "/auth/refresh", "/pay-success",
		"/user/login", "/user/register", "/api/auth/oauth/**"
	);

	private final CustomDetailsService customDetailsService;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2FailureHandler oAuth2FailureHandler;
	private final BlackListService blackListService;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
		return web -> web.ignoring()
			.requestMatchers("/error", "/favicon.ico");
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws
		Exception {

		AdminAuthenticationFilter adminAuthFilter = new AdminAuthenticationFilter(authenticationManager,
			customDetailsService, blackListService);
		adminAuthFilter.setFilterProcessesUrl("/admin/login");

		UserAuthenticationFilter userAuthFilter = new UserAuthenticationFilter(authenticationManager,
			customDetailsService, blackListService);
		userAuthFilter.setFilterProcessesUrl("/user/login");

		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

		http
			.authenticationProvider(authenticationProvider());


		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(WHITELIST.toArray(new String[0])).permitAll()
				.requestMatchers("/auth/admin/**").hasRole("ADMIN")
				.requestMatchers("/auth/operator/**").hasAnyRole("OPERATOR", "ADMIN")
				.requestMatchers("/auth/semi/**").hasAnyRole("SEMI_OPERATOR", "SEMI_ADMIN")
				.requestMatchers("/auth/social/**").hasRole("SOCIAL")
				.requestMatchers("/auth/user/**", "/payments/**").hasRole("USER")
				.requestMatchers("/auth/allUser/**").hasAnyRole("SOCIAL", "USER")
				.anyRequest().authenticated()
			);

		http
			.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()));

		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);

		http
			.addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(adminAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(userAuthFilter, UsernamePasswordAuthenticationFilter.class);
		http
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
					.userService(customOAuth2UserService))
				.successHandler(oAuth2SuccessHandler)
				.failureHandler(oAuth2FailureHandler)
			);

		http
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login") // 로그아웃 후 리다이렉트 URL 설정
				.deleteCookies("SESSION")   // 쿠키 삭제 (쿠키 이름이 "SESSION"인 경우)
				.invalidateHttpSession(true)
			);

		return http.build();
	}

	private CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(CORS_WHITELIST);
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Collections.singletonList("*"));
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}

package comatching.comatching3.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import comatching.comatching3.admin.auth.filter.AdminAuthenticationFilter;
import comatching.comatching3.admin.auth.service.AdminUserDetailsService;
import comatching.comatching3.users.auth.jwt.JwtExceptionFilter;
import comatching.comatching3.users.auth.jwt.JwtFilter;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.users.auth.oauth2.handler.OAuth2FailureHandler;
import comatching.comatching3.users.auth.oauth2.handler.OAuth2SuccessHandler;
import comatching.comatching3.users.auth.oauth2.service.CustomOAuth2UserService;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.users.service.BlackListService;
import comatching.comatching3.util.CookieUtil;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final List<String> CORS_WHITELIST = List.of(
		"http://localhost:5173"
	);
	private static final List<String> WHITELIST = List.of(
		"/login", "/admin/**", "/charge-monitor/**", "/app/**",
		"/api/participations", "/auth/refresh", "/pay-success", "/"
	);
	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;
	private final AdminUserDetailsService adminUserDetailsService;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2FailureHandler oAuth2FailureHandler;
	private final CookieUtil cookieUtil;
	private final BlackListService blackListService;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
		return web -> web.ignoring()
			.requestMatchers("/error", "/favicon.ico");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
		AuthenticationConfiguration authenticationConfiguration) throws Exception {
		AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

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

		// 관리자 인증 필터 설정
		AdminAuthenticationFilter adminAuthFilter = new AdminAuthenticationFilter(authenticationManager,
			adminUserDetailsService, jwtUtil,
			cookieUtil, refreshTokenService, blackListService);
		adminAuthFilter.setFilterProcessesUrl("/admin/login"); // 관리자 로그인 URL 설정

		http
			.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()));

		http
			.csrf(AbstractHttpConfigurer::disable)
			//todo: 로그인 페이지 등록 필요
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);

		http
			.addFilterBefore(adminAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(new JwtFilter(jwtUtil),
				OAuth2LoginAuthenticationFilter.class)
			.addFilterBefore(new JwtExceptionFilter(), JwtFilter.class);

		http
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
					.userService(customOAuth2UserService))
				.successHandler(oAuth2SuccessHandler)
				.failureHandler(oAuth2FailureHandler)
			);

		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
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

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}

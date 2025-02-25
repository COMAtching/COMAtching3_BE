package comatching.comatching3.auth.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import comatching.comatching3.auth.service.CustomDetailsService;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final ObjectMapper mapper = new ObjectMapper();
	protected final AuthenticationManager authenticationManager;
	protected final CustomDetailsService customDetailsService;

	public AbstractAuthenticationFilter(AuthenticationManager authenticationManager,
		CustomDetailsService customDetailsService) {
		this.authenticationManager = authenticationManager;
		this.customDetailsService = customDetailsService;
		setFilterProcessesUrl(getLoginUrl());
	}

	protected Map<String, String> parseRequest(HttpServletRequest request) throws IOException {
		return mapper.readValue(request.getInputStream(), Map.class);
	}

	protected abstract String getLoginUrl();

	protected String getUsernameKey(Map<String, String> creds) {
		return creds.get("accountId");

	}

	protected String getPasswordKey(Map<String, String> creds) {
		return creds.get("password");
	}

	protected void sendAuthenticationFailed(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write(Response.errorResponse(ResponseCode.INVALID_LOGIN).convertToJson());
		response.getWriter().flush();
	}
}

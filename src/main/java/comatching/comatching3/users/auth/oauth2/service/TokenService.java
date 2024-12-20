package comatching.comatching3.users.auth.oauth2.service;

import org.springframework.stereotype.Service;

import comatching.comatching3.admin.dto.response.TokenRes;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final JwtUtil jwtUtil;

	public TokenRes makeTokenRes(String userUuid, String userRole) {
		String accessToken = jwtUtil.generateAccessToken(userUuid, userRole);
		String refreshToken = jwtUtil.generateRefreshToken(userUuid, userRole);

		return TokenRes.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}

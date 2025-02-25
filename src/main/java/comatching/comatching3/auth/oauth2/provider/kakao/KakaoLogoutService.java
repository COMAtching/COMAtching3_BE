package comatching.comatching3.auth.oauth2.provider.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import comatching.comatching3.auth.oauth2.service.LogoutService;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoLogoutService implements LogoutService {
	private final String AUTHORIZATION = "Authorization";
	private final String KAKAO_ADMIN_KEY_PREFIX = "KakaoAK ";

	private final RestTemplate restTemplate;
	private final SecurityUtil securityUtil;

	@Value("${oauth2.kakao.logout-url}")
	private String kakaoLogoutURL;
	@Value("${oauth2.kakao.admin-key}")
	private String kakaoAdminKey;

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public void logout(Users user) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.add(AUTHORIZATION, KAKAO_ADMIN_KEY_PREFIX + kakaoAdminKey);

		String body = String.format("target_id_type=user_id&target_id=%s", user.getSocialId());

		HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

		ResponseEntity<String> response = restTemplate.exchange(
			kakaoLogoutURL,
			HttpMethod.POST,
			requestEntity,
			String.class
		);

		if (response.getStatusCode().is2xxSuccessful()) {
			System.out.println("Kakao logout successful for user ID: " + user.getSocialId());
		} else {
			System.err.println("Kakao logout failed for user ID: " + user.getSocialId());
		}
	}
}

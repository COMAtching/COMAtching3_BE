package comatching.comatching3.auth.oauth2.provider.kakao;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoApiService {

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String KakaoClientId;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String KakaoClientSecret;

	public KakaoUser getUserInfo(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		headers.set("Authorization", "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Map> response = restTemplate.exchange(
			"https://kapi.kakao.com/v2/user/me",
			HttpMethod.GET,
			entity,
			Map.class
		);

		if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
			Map<String, Object> attributes = response.getBody();

			// 2. 기본 권한을 부여하고, "id"를 key attribute로 사용하여 OAuth2User 생성
			List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_SOCIAL"));
			OAuth2User oAuth2User = new DefaultOAuth2User(authorities, attributes, "id");

			// 3. ClientRegistration은 애플리케이션 설정에 따라 구성되어야 합니다.
			// 예시로, 미리 구성된 kakaoClientRegistration을 사용한다고 가정합니다.
			ClientRegistration clientRegistration = getKakaoClientRegistration(); // 구현 필요

			// 4. KakaoUser 생성 후 리턴
			return new KakaoUser(oAuth2User, clientRegistration);
		} else {
			throw new RuntimeException("카카오 사용자 정보를 불러오지 못했습니다. 상태 코드: " + response.getStatusCode());
		}
	}

	private ClientRegistration getKakaoClientRegistration() {
		return ClientRegistration.withRegistrationId("kakao")
			.clientId(KakaoClientId)
			.clientSecret(KakaoClientSecret)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUri("http://localhost:8080/login/oauth2/code/kakao")
			.authorizationUri("https://kauth.kakao.com/oauth/authorize")
			.tokenUri("https://kauth.kakao.com/oauth/token")
			.userInfoUri("https://kapi.kakao.com/v2/user/me")
			.userNameAttributeName("id")
			.clientName("Kakao")
			.build();
	}
}

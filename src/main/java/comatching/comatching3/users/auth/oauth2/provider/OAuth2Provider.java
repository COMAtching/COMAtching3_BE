package comatching.comatching3.users.auth.oauth2.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {
	KAKAO("kakao");

	private final String registrationId;
}

package comatching.comatching3.auth.oauth2.provider.apple;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import io.jsonwebtoken.Jwts;

@Component
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

	private OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter;

	public CustomRequestEntityConverter() {
		defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
	}

	private final String APPLE_URL = "https://appleid.apple.com";

	@Value("${apple.key-path}")
	private String APPLE_KEY_PATH;

	@Value("${spring.security.oauth2.client.registration.apple.client-id}")
	private String APPLE_CLIENT_ID;

	@Value("${apple.team-id}")
	private String APPLE_TEAM_ID;

	@Value("${apple.key-id}")
	private String APPLE_KEY_ID;

	@Override
	public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest req) {
		RequestEntity<?> entity = defaultConverter.convert(req);
		String registrationId = req.getClientRegistration().getRegistrationId();
		MultiValueMap<String, String> params = (MultiValueMap<String, String>) entity.getBody();
		if (registrationId.contains("apple")) {
			try {
				params.set("client_secret", createClientSecret());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return new RequestEntity<>(params, entity.getHeaders(),
			entity.getMethod(), entity.getUrl());
	}

	public String createClientSecret() throws IOException {
		Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
		Map<String, Object> jwtHeader = new HashMap<>();
		jwtHeader.put("kid", APPLE_KEY_ID);
		jwtHeader.put("alg", "ES256");

		return Jwts.builder()
			.setHeaderParams(jwtHeader)
			.setIssuer(APPLE_TEAM_ID)
			.setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간
			.setExpiration(expirationDate) // 만료 시간
			.setAudience(APPLE_URL)
			.setSubject(APPLE_CLIENT_ID)
			.signWith(getPrivateKey())
			.compact();
	}

	public PrivateKey getPrivateKey() throws IOException {
		ClassPathResource resource = new ClassPathResource(APPLE_KEY_PATH);
		// 배포시 jar 파일을 찾지 못함
		//String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));

		InputStream in = resource.getInputStream();
		PEMParser pemParser = new PEMParser(new StringReader(IOUtils.toString(in, StandardCharsets.UTF_8)));
		PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		return converter.getPrivateKey(object);
	}
}

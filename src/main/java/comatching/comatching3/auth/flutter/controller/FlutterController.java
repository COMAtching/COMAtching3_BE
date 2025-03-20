package comatching.comatching3.auth.flutter.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.auth.details.CustomUser;
import comatching.comatching3.auth.dto.LoginDto;
import comatching.comatching3.auth.flutter.dto.FlutterLoginRes;
import comatching.comatching3.auth.oauth2.provider.kakao.KakaoApiService;
import comatching.comatching3.auth.oauth2.provider.kakao.KakaoUser;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FlutterController {

	private final KakaoApiService kakaoApiService;
	private final UsersRepository usersRepository;
	private final UserAiFeatureRepository userAiFeatureRepository;

	@PostMapping("/api/auth/oauth/kakao")
	public Response<FlutterLoginRes> kakaoLogin(@RequestBody Map<String, String> payload, HttpServletRequest request) {
		String accessToken = payload.get("accessToken");

		if (accessToken == null) {
			throw new BusinessException(ResponseCode.INVALID_LOGIN);
		}

		KakaoUser kakaoUser;

		try {
			kakaoUser = kakaoApiService.getUserInfo(accessToken);
		} catch (Exception e) {
			throw new BusinessException(ResponseCode.INVALID_LOGIN);
		}

		Optional<Users> userOpt = usersRepository.findBySocialId(kakaoUser.getSocialId());
		Users user;
		if (userOpt.isPresent()) {
			user = userOpt.get();
		} else {
			user = registerUser(kakaoUser.getSocialId(), kakaoUser.getProvider(), kakaoUser.getEmail());
		}

		LoginDto loginDto = LoginDto.builder()
			.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
			.role(user.getRole())
			.build();

		CustomUser customUser = new CustomUser(loginDto);

		UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		HttpSession session = request.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

		FlutterLoginRes loginRes = FlutterLoginRes.builder()
			.socialId(user.getSocialId())
			.role(user.getRole())
			.build();

		return Response.ok(loginRes);
	}

	@PostMapping("/api/auth/oauth/naver")
	public Response<FlutterLoginRes> naverLogin(@RequestBody Map<String, String> payload, HttpServletRequest request) {
		if (payload == null) {
			throw new BusinessException(ResponseCode.INVALID_LOGIN);
		}

		String socialId = payload.get("id");
		String email = payload.get("email");
		String provider = "naver";

		Optional<Users> userOpt = usersRepository.findBySocialId(socialId);
		Users user;
		if (userOpt.isPresent()) {
			user = userOpt.get();
		} else {
			user = registerUser(socialId, provider, email);
		}

		LoginDto loginDto = LoginDto.builder()
			.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
			.role(user.getRole())
			.build();

		CustomUser customUser = new CustomUser(loginDto);

		UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		HttpSession session = request.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

		FlutterLoginRes loginRes = FlutterLoginRes.builder()
			.socialId(user.getSocialId())
			.role(user.getRole())
			.build();

		return Response.ok(loginRes);
	}

	@PostMapping("/api/auth/oauth/google")
	public Response<FlutterLoginRes> googleLogin(@RequestBody Map<String, String> payload, HttpServletRequest request) {
		if (payload == null) {
			throw new BusinessException(ResponseCode.INVALID_LOGIN);
		}

		String socialId = payload.get("id");
		String email = payload.get("email");
		String provider = "google";

		Optional<Users> userOpt = usersRepository.findBySocialId(socialId);
		Users user;
		if (userOpt.isPresent()) {
			user = userOpt.get();
		} else {
			user = registerUser(socialId, provider, email);
		}

		LoginDto loginDto = LoginDto.builder()
			.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
			.role(user.getRole())
			.build();

		CustomUser customUser = new CustomUser(loginDto);

		UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		HttpSession session = request.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

		FlutterLoginRes loginRes = FlutterLoginRes.builder()
			.socialId(user.getSocialId())
			.role(user.getRole())
			.build();

		return Response.ok(loginRes);
	}

	private Users registerUser(String socialId, String provider, String email) {
		Users newUser = Users.builder()
			.socialId(socialId)
			.provider(provider)
			.email(email)
			.role(Role.SOCIAL.getRoleName())
			.build();

		byte[] uuid = UUIDUtil.createUUID();
		UserAiFeature userAiFeature = UserAiFeature.builder()
			.users(newUser)
			.uuid(uuid)
			.build();
		newUser.updateUserAiFeature(userAiFeature);

		usersRepository.save(newUser);
		userAiFeatureRepository.save(userAiFeature);

		return newUser;
	}
}

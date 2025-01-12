package comatching.comatching3.admin.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PasswordViewController {

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * 비밀번호 재설정 폼 보여주는 메소드
	 * @param token 레디스 토큰 값
	 * @return 비밀번호 재설정 폼
	 */
	@GetMapping("/admin/reset-password")
	public ModelAndView showResetPasswordForm(@RequestParam String token) {

		Object emailObj = redisTemplate.opsForValue().get(token);

		if (emailObj == null) {
			return new ModelAndView("admin_password/tokenExpired");
		}

		ModelAndView modelAndView = new ModelAndView("admin_password/resetPasswordForm");
		modelAndView.addObject("token", token);
		modelAndView.addObject("message", "Please enter your new password");

		return modelAndView;
	}
}

package comatching.comatching3.util.Idempotent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import comatching.comatching3.util.Idempotent.Exception.IdempotentException;
import comatching.comatching3.util.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

	private final StringRedisTemplate stringRedisTemplate;

	@Pointcut("@annotation(idempotent)")
	public void pointCut(Idempotent idempotent) {
	}

	@Before(value = "pointCut(idempotent)", argNames = "joinPoint, idempotent")
	public void before(JoinPoint joinPoint, Idempotent idempotent) throws IOException {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();

		String requestKey = getRequestKey(request);
		String requestValue = getRequestValue(request);

		int expireTime = idempotent.expireTime();

		Boolean isPoss = stringRedisTemplate
			.opsForValue()
			.setIfAbsent(requestKey, requestValue, expireTime, TimeUnit.SECONDS);

		if (Boolean.FALSE.equals(isPoss)) {
			handleRequestException(requestKey, requestValue);
		}

	}

	private String getRequestKey(final HttpServletRequest request) {
		String token = request.getHeader("requestKey");

		if (token == null)
			throw new IllegalArgumentException();

		return token;
	}

	private String getRequestValue(final HttpServletRequest request) {
		if (!"GET".equalsIgnoreCase(request.getMethod())) {
			ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper)request;

			return new String(cachingRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
		} else {
			return request.getQueryString();
		}
	}

	private void handleRequestException(final String requestKey, final String requestValue) {

		String originRequestValue = stringRedisTemplate.opsForValue().get(requestKey);

		//요청이 내용이 비어있지 않으면서 원래 요청이랑 같은 경우
		if (!requestValue.isBlank() && !requestValue.equals(originRequestValue))
			throw new IdempotentException(ResponseCode.UNPROCESSABLE_ENTITY);

			//요청이 같지 않은 경우
		else
			throw new IdempotentException(ResponseCode.CONFLICT);
	}
}

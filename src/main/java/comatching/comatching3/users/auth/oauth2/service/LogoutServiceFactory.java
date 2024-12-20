package comatching.comatching3.users.auth.oauth2.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class LogoutServiceFactory {

	private final Map<String, LogoutService> serviceMap;

	public LogoutServiceFactory(List<LogoutService> services) {
		this.serviceMap = services.stream()
			.collect(Collectors.toMap(LogoutService::getProvider, service -> service));
	}

	public LogoutService getLogoutService(String provider) {
		LogoutService service = serviceMap.get(provider.toLowerCase());
		if (service == null) {
			throw new IllegalArgumentException("Unsupported provider: " + provider);
		}
		return service;
	}
}

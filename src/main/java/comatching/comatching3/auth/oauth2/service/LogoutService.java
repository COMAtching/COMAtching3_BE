package comatching.comatching3.auth.oauth2.service;

import comatching.comatching3.users.entity.Users;

public interface LogoutService {
	void logout(Users user);

	String getProvider();
}

package comatching.comatching3.users.auth.jwt;

import java.util.concurrent.TimeUnit;

public abstract class JwtExpirationConst {
	public static final long REFRESH_TOKEN_EXPIRATION = TimeUnit.DAYS.toMillis(3);
	public static final long USER_ACCESS_TOKEN_EXPIRATION = TimeUnit.MINUTES.toMillis(60);
	public static final long ADMIN_ACCESS_TOKEN_EXPIRATION = TimeUnit.MINUTES.toMillis(60);
}

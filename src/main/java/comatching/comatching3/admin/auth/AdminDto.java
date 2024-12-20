package comatching.comatching3.admin.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDto {
	private String accountId;
	private String password;
	private String role;
	private String uuid;
}

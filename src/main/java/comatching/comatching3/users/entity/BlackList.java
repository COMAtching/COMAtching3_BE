package comatching.comatching3.users.entity;

import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlackList extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "blackList_id")
	private Long id;

	private byte[] uuid;

	private String provider;

	private String username;

	private String email;

	private String role;

	private String reason;

	@Builder
	public BlackList(byte[] uuid, String provider, String username, String email, String role, String reason) {
		this.uuid = uuid;
		this.provider = provider;
		this.username = username;
		this.email = email;
		this.role = role;
		this.reason = reason;
	}
}

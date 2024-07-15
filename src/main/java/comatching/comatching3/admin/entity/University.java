package comatching.comatching3.admin.entity;

import java.util.List;

import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class University extends BaseEntity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "university_id")
	@Id
	private Long id;

	@OneToMany(mappedBy = "university", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Users> users;

	@OneToMany(mappedBy = "university", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Admin> admins;

	private String mailDomain;

	private String s3Key;

	private String appName;

	@Builder
	public University(String mailDomain, String s3Key, String appName) {
		this.mailDomain = mailDomain;
		this.s3Key = s3Key;
		this.appName = appName;
	}
}

package comatching.comatching3.admin.entity;

import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admin_id")
	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "university_id")
	private University university;

	@Enumerated(value = EnumType.STRING)
	private AdminRole adminRole;

	private String accountId;

	private String password;

	private String email;

	private Boolean universityAuth = false;

	@Builder
	public Admin(University university, AdminRole adminRole, String accountId, String password, String email, Boolean universityAuth) {
		this.university = university;
		this.adminRole = adminRole;
		this.accountId = accountId;
		this.password = password;
		this.email = email;
		this.universityAuth = universityAuth;
	}
}

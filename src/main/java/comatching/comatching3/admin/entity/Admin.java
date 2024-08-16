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

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admin_id")
	@Id
	private Long id;

	@Column(columnDefinition = "BINARY(16)")
	private byte[] uuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "university_id")
	private University university;

	@Enumerated(value = EnumType.STRING)
	private AdminRole adminRole;

	private String accountId;

	private String password;

	private String nickname;

	private String schoolEmail;

	private Boolean universityAuth = false;

	private String contactEmail = null;

	private Boolean accountIdChanged = false;

	private Boolean access = false;

	@Builder
	public Admin(byte[] uuid,String nickname, AdminRole adminRole, String accountId, String password, University university) {
		this.uuid = uuid;
		this.nickname = nickname;
		this.adminRole = adminRole;
		this.accountId = accountId;
		this.password = password;
		this.university = university;
	}

	public void changeAdminRole(AdminRole adminRole) {
		this.adminRole = adminRole;
	}

	public void setSchoolEmail(String schoolEmail) {
		this.schoolEmail = schoolEmail;
	}

	public Optional<String> getContactEmail() {
		return Optional.ofNullable(contactEmail);
	}

	public void updateAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void accountIdChange() {
		this.accountIdChanged = true;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updateContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public void accessOk() {
		this.access = true;
	}
}

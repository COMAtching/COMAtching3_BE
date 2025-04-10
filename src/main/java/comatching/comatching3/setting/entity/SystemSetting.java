package comatching.comatching3.setting.entity;

import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemSetting extends BaseEntity {

	@Id
	@Column(name = "setting_key")
	private String key;

	private String value;

	@Builder
	public SystemSetting(String key, String value) {
		this.key = key;
		this.value = value;
	}
}

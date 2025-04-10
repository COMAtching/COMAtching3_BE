package comatching.comatching3.setting.service;

import org.springframework.stereotype.Service;

import comatching.comatching3.setting.entity.SystemSetting;
import comatching.comatching3.setting.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemSettingService {

	private static final String BALANCE_BUTTON_ENABLED = "balance_button_enabled";

	private final SystemSettingRepository systemSettingRepository;

	public boolean isBalanceButtonEnabled() {
		return systemSettingRepository.findByKey(BALANCE_BUTTON_ENABLED)
			.map(setting -> "true".equals(setting.getValue()))
			.orElse(false); // 기본값은 비활성화
	}

	public void setBalanceButtonEnabled(boolean enabled) {
		SystemSetting setting = systemSettingRepository.findByKey(BALANCE_BUTTON_ENABLED)
			.orElse(SystemSetting.builder()
				.key(BALANCE_BUTTON_ENABLED)
				.value("false")
				.build());

		setting.setKey(BALANCE_BUTTON_ENABLED);
		setting.setValue(enabled ? "true" : "false");
		systemSettingRepository.save(setting);
	}
}

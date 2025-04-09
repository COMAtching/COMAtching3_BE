package comatching.comatching3.event.batch;

import org.springframework.batch.item.ItemProcessor;

import comatching.comatching3.users.entity.Users;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DailyPointItemProcessor implements ItemProcessor<Users, Users> {

	@Override
	public Users process(Users user) {
		// dailyPoint 값을 0으로 초기화
		user.setDailyPoint(0L);
		log.debug("Reset dailyPoint to 0 for user: {}", user.getId());
		return user;
	}
}

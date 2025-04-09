package comatching.comatching3.pay.batch;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import comatching.comatching3.event.batch.DailyPointItemProcessor;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableScheduling
@RequiredArgsConstructor
public class DailyPointBatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final UsersRepository usersRepository;
	private final JobLauncher jobLauncher;

	private static final int CHUNK_SIZE = 100;

	@Bean
	public Job resetDailyPointJob() {
		return new JobBuilder("resetDailyPointJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(resetDailyPointStep())
			.build();
	}

	@Bean
	public Step resetDailyPointStep() {
		return new StepBuilder("resetDailyPointStep", jobRepository)
			.<Users, Users>chunk(CHUNK_SIZE, transactionManager)
			.reader(dailyPointItemReader())
			.processor(dailyPointItemProcessor())
			.writer(dailyPointItemWriter())
			.build();
	}

	@Bean
	public RepositoryItemReader<Users> dailyPointItemReader() {
		Map<String, Sort.Direction> sortMap = new HashMap<>();
		sortMap.put("id", Sort.Direction.ASC);

		return new RepositoryItemReaderBuilder<Users>()
			.name("dailyPointItemReader")
			.repository(usersRepository)
			.methodName("findAll")
			.arguments(Collections.emptyList())
			.sorts(sortMap)
			.pageSize(CHUNK_SIZE)
			.build();
	}

	@Bean
	public DailyPointItemProcessor dailyPointItemProcessor() {
		return new DailyPointItemProcessor();
	}

	@Bean
	public RepositoryItemWriter<Users> dailyPointItemWriter() {
		RepositoryItemWriter<Users> writer = new RepositoryItemWriter<>();
		writer.setRepository(usersRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Scheduled(cron = "0 43 12 * * ?", zone = "Asia/Seoul")
	public void runResetDailyPointJob() {
		try {
			log.info("Daily Point Reset Job started");
			jobLauncher.run(resetDailyPointJob(), new org.springframework.batch.core.JobParameters());
			log.info("Daily Point Reset Job completed");
		} catch (Exception e) {
			log.error("Error occurred while running Daily Point Reset Job", e);
		}
	}
}

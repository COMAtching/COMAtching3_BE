package comatching.comatching3.event.batch;

import comatching.comatching3.event.entity.Event;
import comatching.comatching3.event.entity.EventParticipation;
import comatching.comatching3.event.repository.EventParticipationRepository;
import comatching.comatching3.event.repository.EventRepository;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@EnableBatchProcessing
public class CreateEventBatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    @Bean
    public Job createEventParticipationJob() {
        // Job 생성
        return new JobBuilder("createEventParticipationJob", jobRepository)
                .start(createEventParticipationStep())
                .build();
    }

    @Bean
    public Step createEventParticipationStep() {
        // Step 생성
        return new StepBuilder("createEventParticipationStep", jobRepository)
                .<Users, EventParticipation>chunk(100, platformTransactionManager)
                .reader(userReader(null))  // Reader
                .processor(eventParticipationProcessor(null))  // Processor
                .writer(eventParticipationWriter())  // Writer
                .build();
    }

    // Reader: universityId로 특정 대학의 유저 조회
    @Bean
    @StepScope
    public ItemReader<Users> userReader(@Value("#{jobParameters['universityId']}") Long universityId) {
        return new ItemReader<Users>() {
            private List<Users> users;
            private int index;

            @Override
            public Users read() throws Exception {
                if (users == null) {
                    users = userRepository.findAllUserByUniversityId(universityId);  // universityId로 유저 조회
                    index = 0;
                }
                if (index < users.size()) {
                    return users.get(index++);
                }
                return null;
            }
        };
    }

    // Processor: 각 유저에 대해 EventParticipation을 생성
    @Bean
    @StepScope
    public ItemProcessor<Users, EventParticipation> eventParticipationProcessor(
            @Value("#{jobParameters['eventId']}") Long eventId) {
        return new ItemProcessor<Users, EventParticipation>() {
            @Override
            public EventParticipation process(Users user) throws Exception {
                Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new Exception("Event not found"));

                EventParticipation participation = new EventParticipation();
                participation.setUser(user);
                participation.setEvent(event);
                participation.setParticipated(false);
                return participation;
            }
        };
    }

    // Writer: EventParticipation을 DB에 저장
    @Bean
    public ItemWriter<EventParticipation> eventParticipationWriter() {
        return new ItemWriter<EventParticipation>() {
            @Override
            public void write(Chunk<? extends EventParticipation> chunk) throws Exception {
                eventParticipationRepository.saveAll(chunk);
            }
        };
    }
}

package comatching.comatching3.history.entity;

import comatching.comatching3.matching.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.matching.dto.request.MatchReq;
import comatching.comatching3.matching.enums.AgeOption;
import comatching.comatching3.matching.enums.ContactFrequencyOption;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.HobbyEnum;
import comatching.comatching3.util.BaseEntity;
import comatching.comatching3.util.HobbyListConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingHistory extends BaseEntity {
    @Id
    @Column(name = "comatch_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "applier_info_id")
    private Users applier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enemy_info_id")
    private Users enemy;

    @Column(length = 4)
    private String mbtiOption;

    @Enumerated(value = EnumType.STRING)
    private AgeOption ageOption;

    @Enumerated(value = EnumType.STRING)
    private ContactFrequencyOption contactFrequencyOption;

    @Convert(converter = HobbyListConverter.class)
    private List<HobbyEnum> hobbyEnumOption;

    private Boolean noSameMajorOption;

    private String importantOption;

    @Builder
    public MatchingHistory(Users applier, Users enemy, String mbtiOption, AgeOption ageOption,
                           ContactFrequencyOption contactFrequencyOption, List<HobbyEnum> hobbyEnumOption, Boolean noSameMajorOption) {
        this.applier = applier;
        this.enemy = enemy;
        this.mbtiOption = mbtiOption;
        this.ageOption = ageOption;
        this.contactFrequencyOption = contactFrequencyOption;
        this.hobbyEnumOption = hobbyEnumOption;
        this.noSameMajorOption = noSameMajorOption;

    }

    public void updateOptionsFromRequestMsg(MatchRequestMsg matchRequestMsg) {
        this.noSameMajorOption = matchRequestMsg.getSameMajorOption();
        this.hobbyEnumOption = matchRequestMsg.getHobbyOption();
        this.ageOption = matchRequestMsg.getAgeOption();
        this.mbtiOption = matchRequestMsg.getMbtiOption();
        this.contactFrequencyOption = matchRequestMsg.getContactFrequencyOption();
        this.importantOption = matchRequestMsg.getImportantOption();
    }

    public void updateOptionsFromRequestMsg(MatchReq matchReq) {
        this.noSameMajorOption = matchReq.getSameMajorOption();
        this.hobbyEnumOption = matchReq.getHobbyOption();
        this.ageOption = matchReq.getAgeOption();
        this.mbtiOption = matchReq.getMbtiOption();
        this.contactFrequencyOption = matchReq.getContactFrequencyOption();
        this.importantOption = matchReq.getImportantOption();
    }

    @Override
    public String toString() {
        return "MatchingHistory{" +
                "id=" + id +
                ", applier=" + (applier != null ? applier.getId() : "null") +
                ", enemy=" + (enemy != null ? enemy.getId() : "null") +
                ", mbtiOption='" + mbtiOption + '\'' +
                ", ageOption=" + ageOption +
                ", contactFrequencyOption=" + contactFrequencyOption +
                ", hobbyOption=" + hobbyEnumOption +
                ", noSameMajorOption=" + noSameMajorOption +
                '}';
    }


}

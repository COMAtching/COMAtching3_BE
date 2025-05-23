package comatching.comatching3.matching.dto.messageQueue;

import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.matching.dto.request.MatchReq;
import comatching.comatching3.matching.enums.AgeOption;
import comatching.comatching3.matching.enums.ContactFrequencyOption;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.enums.HobbyEnum;
import comatching.comatching3.util.UUIDUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MatchRequestMsg {

    private String matcherUuid;
    private ContactFrequencyOption contactFrequencyOption;
    private String genderOption;
    private ArrayList<HobbyEnum> hobbyOption = new ArrayList<HobbyEnum>();
    private Boolean sameMajorOption;
    private AgeOption ageOption;
    private String mbtiOption;
    private String myMajor;
    private Integer myAge;
    private String university;
    private String importantOption;
    private List<String> duplicationList = new ArrayList<String>();
    private Double mbtiWeight = 0.25;
    private Double ageWeight = 0.25;
    private Double hobbyWeight = 0.25;
    private Double contactFrequencyWeight = 0.25;


    public void fromMatchReqAndUserAiFeature(MatchReq matchReq, UserAiFeature applierFeature, String university) {
        this.matcherUuid = UUIDUtil.bytesToHex(applierFeature.getUuid());
        this.contactFrequencyOption = matchReq.getContactFrequencyOption();
        this.genderOption = applierFeature.getGender().getAiValue();
        this.hobbyOption = matchReq.getHobbyOption();
        this.sameMajorOption = matchReq.getSameMajorOption();
        this.ageOption = matchReq.getAgeOption();
        this.mbtiOption = matchReq.getMbtiOption();
        this.myMajor = applierFeature.getMajor();
        this.myAge = applierFeature.getAge();
        this.university = university;
        this.importantOption = matchReq.getImportantOption();
    }

    public void updateWeight() {
        switch (this.importantOption) {
            case "ageOption" -> {
                this.mbtiWeight = 0.2;
                this.ageWeight = 0.4;
                this.hobbyWeight = 0.2;
                this.contactFrequencyWeight = 0.2;
            }
            case "mbtiOption" -> {
                this.mbtiWeight = 0.4;
                this.ageWeight = 0.2;
                this.hobbyWeight = 0.2;
                this.contactFrequencyWeight = 0.2;
            }
            case "hobbyOption" -> {
                this.mbtiWeight = 0.2;
                this.ageWeight = 0.2;
                this.hobbyWeight = 0.4;
                this.contactFrequencyWeight = 0.2;
            }
            case "contactFrequencyOption" -> {
                this.mbtiWeight = 0.2;
                this.ageWeight = 0.2;
                this.hobbyWeight = 0.2;
                this.contactFrequencyWeight = 0.4;
            }
        }
    }

    public void updateDuplicationListFromHistory(List<MatchingHistory> matchingHistories) {
        for (MatchingHistory history : matchingHistories) {
            String uuid = UUIDUtil.bytesToHex(history.getEnemy().getUserAiFeature().getUuid());
            this.duplicationList.add(uuid);
        }
    }
}

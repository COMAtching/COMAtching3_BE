package comatching.comatching3.history.dto.res;

import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import lombok.Getter;

import java.util.List;

@Getter
public class MatchHistoryRes {

    private String mbti;
    private ContactFrequency contactFrequency;
    private List<String> hobbyList;
    private Integer age;
    private Gender gender;
    private String major;
    private String comment;
    private String song;
    private String contactId;

    public void updateFromUsers(Users users) {
        UserAiFeature userAiFeature = users.getUserAiFeature();
        this.mbti = userAiFeature.getMbti();
        this.contactFrequency = userAiFeature.getContactFrequency();
        this.hobbyList = userAiFeature.getHobbyNameList();
        this.age = userAiFeature.getAge();
        this.gender = userAiFeature.getGender();
        this.major = userAiFeature.getMajor();
        this.comment = users.getComment();
        this.song = users.getSong();
        this.contactId = users.getContactId();
    }


}

package comatching.comatching3.users.dto.messageQueue;

import comatching.comatching3.users.entity.Hobby;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.util.UUIDUtil;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCrudMsg {

    private UserCrudType type;
    private String uuid;
    private Integer age;
    private String contactFrequency;
    private String gender;
    private String hobby;
    private String major;
    private String mbti;
    private String duplication = "kim_seung_won_sa_rang_hae";


    public void updateFromUserAIFeatureAndType(UserCrudType type, UserAiFeature userAiFeature) {
        this.type = type;
        this.uuid = UUIDUtil.bytesToHex(userAiFeature.getUuid());
        this.mbti = userAiFeature.getMbti();
        this.contactFrequency = userAiFeature.getContactFrequency().getAiValue();
        this.hobby = toHobbyString(userAiFeature.getHobbyList());
        this.age = userAiFeature.getAge();
        this.gender = userAiFeature.getGender().getAiValue();
        this.major = userAiFeature.getMajor();
    }

    private String toHobbyString(List<Hobby> hobbies) {
        StringBuilder hobbyString = new StringBuilder();
        for (Hobby h : hobbies) {
            hobbyString.append(h.getHobbyName() + ",");
        }

        return hobbyString.toString();
    }
}

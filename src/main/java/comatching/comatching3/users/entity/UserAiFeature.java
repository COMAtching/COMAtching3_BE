package comatching.comatching3.users.entity;

import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAiFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_ai_feature_id")
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private byte[] uuid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    private String mbti;

    @Enumerated(EnumType.STRING)
    private ContactFrequency contactFrequency;

    @OneToMany(mappedBy = "userAiFeature", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hobby> hobbyList = new ArrayList<>();

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String major;

    private boolean dontPickMe = false;


    @Builder
    public UserAiFeature(byte[] uuid, Users users) {
        this.uuid = uuid;
        this.users = users;
    }

    public List<String> getHobbyCategoryList() {
        List<String> hobbyCategoryList = new ArrayList<>();
        for (Hobby hobby : hobbyList) {
            hobbyCategoryList.add(hobby.getCategory());
        }

        return hobbyCategoryList;
    }

    public List<String> getHobbyNameList() {
        List<String> hobbyCategoryList = new ArrayList<>();
        for (Hobby hobby : hobbyList) {
            hobbyCategoryList.add(hobby.getHobbyName());
        }

        return hobbyCategoryList;
    }

    public void updateMbti(String mbti) {
        this.mbti = mbti;
    }

    public void updateContactFrequency(ContactFrequency contactFrequency) {
        this.contactFrequency = contactFrequency;
    }

    public void addHobby(List<Hobby> hobbies) {
        for (Hobby hobby : hobbies) {
            this.hobbyList.add(hobby);
            hobby.setUserAiFeature(this);
        }
    }

    public void removeHobby(List<Hobby> hobbies) {
        for (Hobby hobby : hobbies) {
            this.hobbyList.remove(hobby);
            hobby.setUserAiFeature(null);
        }
    }

    public void updateAge(Integer age) {
        this.age = age;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateMajor(String major) {
        this.major = major;
    }

    public void updateUuid(byte[] uuid) {
        this.uuid = uuid;
    }

    public void updateDontPickMe() {
    	this.dontPickMe = !this.dontPickMe;
    }

}

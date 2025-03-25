package comatching.comatching3.users.entity;

import comatching.comatching3.users.enums.HobbyEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hobby_id")
    private Long id;

    private String hobbyName;

    private HobbyEnum hobbyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_ai_feature_id")
    private UserAiFeature userAiFeature;

    @Builder
    public Hobby(String hobbyName, UserAiFeature userAiFeature, HobbyEnum hobbyType) {
        this.hobbyName = hobbyName;
        this.userAiFeature = userAiFeature;
        this.hobbyType = hobbyType;
    }

    public void setUserAiFeature(UserAiFeature userAiFeature) {
        this.userAiFeature = userAiFeature;
    }
}

package comatching.comatching3.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoRes {
    private String username;
    private String major;
    private Integer age;
    private String song;
    private String mbti;
    private Integer point;
    private Integer pickMe;
    private Boolean canRequestCharge;
}

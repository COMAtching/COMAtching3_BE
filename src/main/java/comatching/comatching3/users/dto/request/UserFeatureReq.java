package comatching.comatching3.users.dto.request;

import java.util.List;

import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class UserFeatureReq {
    private String university;
    private String major;
    private String contactId;
    private String contactType;
    private String gender;
    private String username;
    private String year;
    private String month;
    private String day;
    private String mbti;
    private List<String> hobby;
    private String contactFrequency;
    private String song;
    private String comment;
}

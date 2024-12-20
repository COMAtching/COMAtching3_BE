package comatching.comatching3.users.dto;

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
    private String gender;

    @Min(20) @Max(30)
    private Integer age;
    private String mbti;
    private List<String> hobby;
    private String contactFrequency;
    private String song;
    private String comment;
    private Integer admissionYear;
}

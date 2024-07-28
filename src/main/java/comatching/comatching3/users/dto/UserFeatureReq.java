package comatching.comatching3.users.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

import java.util.List;

@Getter
public class UserFeatureReq {
    private String major;
//    private String contactId; 아직 안정해져서 주석처리
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

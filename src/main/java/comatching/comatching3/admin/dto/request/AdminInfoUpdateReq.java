package comatching.comatching3.admin.dto.request;

import lombok.Getter;

import java.util.Optional;

@Getter
public class AdminInfoUpdateReq {
    private Optional<String> accountId;
    private Optional<String> nickname;
    private Optional<String> contactEmail;
    private Optional<String > appName;
}

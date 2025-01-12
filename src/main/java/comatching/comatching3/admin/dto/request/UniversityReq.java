package comatching.comatching3.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UniversityReq {

    @NotNull @NotBlank
    private String name;

    @NotNull @NotBlank
    private String mailDomain;
}

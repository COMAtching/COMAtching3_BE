package comatching.comatching3.admin.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolEmailReq {

    @Email
    private String schoolEmail;
}

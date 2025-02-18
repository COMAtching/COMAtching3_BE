package comatching.comatching3.auth.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountId;
    private String password;
    private String uuid;
    private String role;
}

package comatching.comatching3.admin.controller;

import comatching.comatching3.admin.dto.AdminLoginForm;
import comatching.comatching3.admin.dto.AdminRegisterForm;
import comatching.comatching3.admin.dto.TokenDto;
import comatching.comatching3.admin.service.AdminService;
import comatching.comatching3.util.Response;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register")
    public Response<?> adminRegister(@RequestBody AdminRegisterForm form) {
        adminService.adminRegister(form);
        return Response.ok();
    }

    @PostMapping("/login")
    public Response<?> adminLogin(@RequestBody AdminLoginForm form,
                           HttpServletResponse response) {

        TokenDto tokens = adminService.adminLogin(form);

        response.addHeader("Authorization", "Bearer " + tokens.getAccessToken());
        response.addHeader("Refresh-Token", tokens.getRefreshToken());

        return Response.ok();
    }

}

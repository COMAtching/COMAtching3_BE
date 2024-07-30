package comatching.comatching3.admin.controller;

import comatching.comatching3.admin.dto.request.AdminLoginReq;
import comatching.comatching3.admin.dto.request.AdminRegisterReq;
import comatching.comatching3.admin.dto.response.TokenRes;
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
    public Response<?> adminRegister(@RequestBody AdminRegisterReq form) {
        adminService.adminRegister(form);
        return Response.ok();
    }

    @PostMapping("/login")
    public Response<?> adminLogin(@RequestBody AdminLoginReq form,
                           HttpServletResponse response) {

        TokenRes tokens = adminService.adminLogin(form);

        response.addHeader("Authorization", "Bearer " + tokens.getAccessToken());
        response.addHeader("Refresh-Token", tokens.getRefreshToken());

        return Response.ok();
    }

}

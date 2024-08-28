package comatching.comatching3.charge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebTestController {

    @GetMapping("/charge-monitor")
    public ModelAndView chargeMonitorPage() {
        // ModelAndView를 사용하여 뷰 이름과 모델 데이터를 반환
        return new ModelAndView("chargeRequest");  // 'charge-monitor.html' 페이지 반환
    }
}

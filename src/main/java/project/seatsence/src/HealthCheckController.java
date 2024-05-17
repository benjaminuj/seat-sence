package project.seatsence.src;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthCheckController {

    @GetMapping("/health-check")
    public String getHealthCheck(Model model) {
        model.addAttribute("data","hello");
        return "HealthCheck";
    }
}

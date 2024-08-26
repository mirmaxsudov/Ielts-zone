package uz.ieltszone.zonelifeservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.ieltszone.zonelifeservice.service.base.RateService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/zone-life/rate")
public class RateController {
    private final RateService rateService;
}
package com.vozhe.jwt.controller.settings;

import com.vozhe.jwt.payload.request.PaymentSettingsDTO;
import com.vozhe.jwt.service.settings.PaymentSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment-settings")
@RequiredArgsConstructor
public class PaymentSettingsController {

    private final PaymentSettingsService service;

    @GetMapping
    public PaymentSettingsDTO getPaymentSettings() {
        return service.getPaymentSettings();
    }

    @PostMapping
    public void savePaymentSettings(@RequestBody PaymentSettingsDTO dto) {
        service.savePaymentSettings(dto);
    }
}

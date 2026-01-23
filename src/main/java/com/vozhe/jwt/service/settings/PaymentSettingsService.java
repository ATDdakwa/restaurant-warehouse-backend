package com.vozhe.jwt.service.settings;

import com.vozhe.jwt.payload.request.PaymentSettingsDTO;

public interface PaymentSettingsService {
    PaymentSettingsDTO getPaymentSettings();
    void savePaymentSettings(PaymentSettingsDTO dto);
}
package com.vozhe.jwt.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaymentSettingsDTO {
    private List<String> currencies;
    private List<String> paymentTypes;
}
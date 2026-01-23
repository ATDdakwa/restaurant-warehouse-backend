package com.vozhe.jwt.service.Impl.settings;

import com.vozhe.jwt.models.settings.Currency;
import com.vozhe.jwt.models.settings.PaymentType;
import com.vozhe.jwt.payload.request.PaymentSettingsDTO;
import com.vozhe.jwt.repository.settings.CurrencyRepository;
import com.vozhe.jwt.repository.settings.PaymentTypeRepository;
import com.vozhe.jwt.service.settings.PaymentSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentSettingsServiceImpl implements PaymentSettingsService {

    private final CurrencyRepository currencyRepository;
    private final PaymentTypeRepository paymentTypeRepository;

    @Override
    public PaymentSettingsDTO getPaymentSettings() {
        PaymentSettingsDTO dto = new PaymentSettingsDTO();

        dto.setCurrencies(
                currencyRepository.findAll()
                        .stream()
                        .map(Currency::getCode)
                        .collect(Collectors.toList())
        );

        dto.setPaymentTypes(
                paymentTypeRepository.findAll()
                        .stream()
                        .map(PaymentType::getName)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    @Override
    public void savePaymentSettings(PaymentSettingsDTO dto) {
        currencyRepository.deleteAll();
        paymentTypeRepository.deleteAll();

        dto.getCurrencies().forEach(code -> {
            Currency currency = new Currency();
            currency.setCode(code);
            currencyRepository.save(currency);
        });

        dto.getPaymentTypes().forEach(name -> {
            PaymentType type = new PaymentType();
            type.setName(name);
            paymentTypeRepository.save(type);
        });
    }
}


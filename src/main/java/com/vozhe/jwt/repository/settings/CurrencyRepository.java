package com.vozhe.jwt.repository.settings;

import com.vozhe.jwt.models.settings.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}

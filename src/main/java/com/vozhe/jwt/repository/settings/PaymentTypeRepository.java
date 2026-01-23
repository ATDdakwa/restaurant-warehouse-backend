package com.vozhe.jwt.repository.settings;

import com.vozhe.jwt.models.settings.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
}


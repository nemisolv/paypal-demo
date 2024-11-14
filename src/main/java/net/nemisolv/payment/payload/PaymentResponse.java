package net.nemisolv.payment.payload;

import net.nemisolv.payment.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        UUID userId,
        UUID subscriptionId,
        BigDecimal amount,
        String currency,
        String description,
        String payerEmail,
        String status,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt
) {
}

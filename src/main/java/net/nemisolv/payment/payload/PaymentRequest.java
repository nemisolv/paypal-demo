package net.nemisolv.payment.payload;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        UUID userId,
        BigDecimal amount,
        String currency,
        String description,
        String payerEmail,
        UUID subscriptionId


) {

}

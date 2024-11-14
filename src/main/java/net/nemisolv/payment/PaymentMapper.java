package net.nemisolv.payment;

import net.nemisolv.payment.entity.Payment;
import net.nemisolv.payment.entity.PaymentMethod;
import net.nemisolv.payment.entity.PaymentStatus;
import net.nemisolv.payment.payload.PaymentRequest;
import net.nemisolv.payment.payload.PaymentResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentMapper {
    public Payment toEntity(PaymentRequest paymentRequest) {
        return  Payment.builder()
                .amount(paymentRequest.amount())
                .currency(paymentRequest.currency())
                .userId(paymentRequest.userId())
                .paymentMethod(PaymentMethod.PAYPAL)
                .createdAt(LocalDateTime.now())
                .payerEmail(paymentRequest.payerEmail())
                .description(paymentRequest.description())
                .subscriptionId(paymentRequest.subscriptionId())
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getUserId(),
                payment.getSubscriptionId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription(),
                payment.getPayerEmail(),
                payment.getStatus(),
                payment.getPaymentMethod(),

                payment.getCreatedAt()
        );
    }
}

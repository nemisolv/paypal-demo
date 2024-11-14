package net.nemisolv.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@AllArgsConstructor @NoArgsConstructor
@Builder
@Getter @Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId; // ID người dùng chatbot thực hiện thanh toán

    @Column(name = "amount", nullable = false)
    private BigDecimal amount; // Số tiền giao dịch

    @Column(name = "currency", nullable = false, length = 3)
    private String currency; // Đơn vị tiền tệ (ví dụ: USD)

    @Column(name = "status", nullable = false)
    private String status; // Trạng thái giao dịch (ví dụ: "COMPLETED")

    @Column(name = "transaction_id", unique = true)
    private String transactionId; // Mã giao dịch từ PayPal

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // Phương thức thanh toán ("PAYPAL")

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // Thời điểm tạo giao dịch

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Thời điểm cập nhật giao dịch

    @Column(name = "payer_email", nullable = false)
    private String payerEmail; // Email người thanh toán (nhận từ PayPal)

    @Column(name = "payer_id")
    private String payerId; // ID người thanh toán từ PayPal  c đ

    @Column(name = "description")
    private String description; // Mô tả giao dịch, ví dụ "Thanh toán cho dịch vụ chatbot"

    @Column(name = "gross_amount")
    private BigDecimal grossAmount; // Số tiền thanh toán ban đầu

    @Column(name = "paypal_fee")
    private BigDecimal paypalFee; // Phí giao dịch PayPal

    @Column(name = "net_amount")
    private BigDecimal netAmount; // Số tiền thực nhận sau khi trừ phí

    @Column(name = "failure_reason")
    private String failureReason; // Lý do thất bại (nếu giao dịch không thành công)

    @Column(name = "subscription_id",nullable = false)
    private UUID subscriptionId; // ID của gói dịch vụ (nếu có)



}

package net.nemisolv.payment.repository;

import net.nemisolv.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository  extends JpaRepository<Payment, UUID> {

    // Tìm kiếm thanh toán theo transactionId
    Optional<Payment> findByTransactionId(String transactionId);

    // Lấy danh sách các thanh toán theo userId
    List<Payment> findByUserId(UUID userId);

    // Lấy danh sách thanh toán theo trạng thái
    List<Payment> findByStatus(String status);
}

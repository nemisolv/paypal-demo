package net.nemisolv.payment;

import net.nemisolv.payment.entity.Payment;
import net.nemisolv.payment.payload.PaymentRequest;
import net.nemisolv.payment.payload.PaymentResponse;
import net.nemisolv.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    // Endpoint để tạo thanh toán mới
    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequest paymentRequest) {
      String url = paymentService.createPayment(paymentRequest);
        return ResponseEntity.ok(url);
    }




    // Endpoint để kiểm tra trạng thái thanh toán dựa trên transactionId
    @GetMapping("/status/{transactionId}")
    public ResponseEntity<Payment> getPaymentStatus(@PathVariable String transactionId) {
        return paymentService.getPaymentByTransactionId(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint để lấy lịch sử thanh toán của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPayments(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }
    @GetMapping("/complete")
    public ResponseEntity<?> completePayment(@RequestParam("token") String token) {
        paymentService.capturePayment(token);
        return ResponseEntity.ok("Payment completed");
    }
    @GetMapping("/cancel")
    public ResponseEntity<?> cancelPayment() {
        return ResponseEntity.ok("Payment canceled");
    }
}

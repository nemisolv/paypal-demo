package net.nemisolv.payment.service;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import lombok.extern.slf4j.Slf4j;
import net.nemisolv.payment.PaymentMapper;
import net.nemisolv.payment.entity.PaymentMethod;
import net.nemisolv.payment.payload.PaymentRequest;
import net.nemisolv.payment.entity.Payment;
import net.nemisolv.payment.payload.PaymentResponse;
import net.nemisolv.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private  final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final PayPalHttpClient payPalHttpClient;


//    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
//        Payment payment = paymentMapper.toEntity(paymentRequest);
//
//        return paymentMapper.toResponse(paymentRepository.save(payment));
//    }


    // Lấy thanh toán theo transactionId
    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }

    // Lấy danh sách thanh toán của một người dùng theo userId
    public List<Payment> getPaymentsByUserId(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    // Lấy danh sách thanh toán theo trạng thái
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    public String createPayment(PaymentRequest paymentRequest) {
        // Thiết lập yêu cầu thanh toán PayPal
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");  // CAPTURE để thanh toán ngay lập tức

        // Thiết lập số tiền thanh toán
        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown()
                .currencyCode(paymentRequest.currency())
                .value(paymentRequest.amount().toString());

        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .amountWithBreakdown(amountWithBreakdown)
                .description(paymentRequest.description());

        List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();
        purchaseUnitRequests.add(purchaseUnitRequest);

        orderRequest.purchaseUnits(purchaseUnitRequests);

        // URL trả về khi hoàn tất hoặc hủy thanh toán
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl("http://localhost:8080/api/payments/complete")  // URL khi hoàn tất
                .cancelUrl("http://localhost:8080/api/payments/cancel");   // URL khi hủy
        orderRequest.applicationContext(applicationContext);

        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            // Thực hiện yêu cầu tạo đơn hàng trên PayPal
            HttpResponse<Order> response = payPalHttpClient.execute(request);
            Order order = response.result();

            // Lưu Payment với transactionId từ PayPal
            Payment payment = new Payment();
            payment.setUserId(paymentRequest.userId());
            payment.setAmount(paymentRequest.amount());
            payment.setCurrency(paymentRequest.currency());
            payment.setDescription(paymentRequest.description());
            payment.setPayerEmail(paymentRequest.payerEmail());
            payment.setPaymentMethod(PaymentMethod.PAYPAL);
            payment.setStatus("CREATED");
            payment.setTransactionId(order.id());  // Lưu order ID từ PayPal
            payment.setCreatedAt(LocalDateTime.now());
            payment.setSubscriptionId(paymentRequest.subscriptionId());
            paymentRepository.save(payment);

            // Lấy approval URL để chuyển người dùng đến PayPal phê duyệt
             var linkURl = order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Approval URL không tìm thấy"))
                    .href();

             log.info("linkURl: " + linkURl);
             log.info("order id: " + order.id());
             return linkURl;


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo thanh toán PayPal: " + e.getMessage());
        }
    }

    // Xác nhận thanh toán PayPal sau khi người dùng phê duyệt
    public PaymentResponse capturePayment(String orderId) {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);

        try {
            // Thực hiện capture (thu tiền) trên PayPal
            HttpResponse<Order> response = payPalHttpClient.execute(request);
            Order order = response.result();

            // Lấy Payment trong cơ sở dữ liệu bằng transactionId
            Payment payment = paymentRepository.findByTransactionId(orderId)
                    .orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại trong cơ sở dữ liệu"));

            // Cập nhật trạng thái và số tiền thực nhận
            payment.setStatus(order.status());
            BigDecimal grossAmount = new BigDecimal(order.purchaseUnits().get(0).payments().captures().get(0).amount().value());
            payment.setGrossAmount(grossAmount);

            BigDecimal paypalFee = new BigDecimal(order.purchaseUnits().get(0).payments().captures().get(0)
                    .sellerReceivableBreakdown().paypalFee().value());
            payment.setPaypalFee(paypalFee);

            payment.setNetAmount(grossAmount.subtract(paypalFee));
            Payment savedPayment = paymentRepository.save(payment);

            return paymentMapper.toResponse(savedPayment);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xác nhận thanh toán PayPal: " + e.getMessage());
        }
    }


}

package com.restaurant.billing.repository;

import com.restaurant.billing.entity.Payment;
import com.restaurant.billing.entity.enums.PaymentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReferenceId(String referenceId);
    Optional<Payment> findByGatewayQrId(String gatewayQrId);
    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);
    List<Payment> findAllByOrderByCreatedAtDesc();

    Optional<Payment> findFirstByInvoice_IdAndStatusOrderByCreatedAtDesc(Long invoiceId, PaymentStatus status);
}


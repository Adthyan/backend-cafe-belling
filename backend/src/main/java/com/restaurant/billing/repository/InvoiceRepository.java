package com.restaurant.billing.repository;

import com.restaurant.billing.entity.Invoice;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findFirstBySale_IdOrderByCreatedAtDesc(Long saleId);
}


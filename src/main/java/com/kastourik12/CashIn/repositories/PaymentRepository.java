package com.kastourik12.CashIn.repositories;

import com.kastourik12.CashIn.models.EPaymentStatus;
import com.kastourik12.CashIn.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    void deleteByPayerId(String payerId);

    @Modifying
    @Query(value = "UPDATE payments SET status = ?2  WHERE payment_id= ?1", nativeQuery = true)
    void updatePaymentStatus(String paymentId, EPaymentStatus status);

    @Modifying
    @Query(value = "UPDATE payments SET payer_id = ?2  WHERE payment_id= ?1", nativeQuery = true)
    void updatePayerId(String paymentId, String payerId);

    Optional<Payment> findByPaymentId(String paymentId);

}




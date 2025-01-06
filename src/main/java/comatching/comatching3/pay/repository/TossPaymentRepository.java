package comatching.comatching3.pay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import comatching.comatching3.pay.entity.TossPayment;

@Repository
public interface TossPaymentRepository extends JpaRepository<TossPayment, Long> {
}

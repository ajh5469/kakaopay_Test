package com.example.demo.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCancelRepository extends JpaRepository<PaymentCancel, Long>{

}

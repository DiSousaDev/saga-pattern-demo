package br.dev.diego.payments.service;

import br.dev.diego.core.dto.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> findAll();

    Payment process(Payment payment);
}

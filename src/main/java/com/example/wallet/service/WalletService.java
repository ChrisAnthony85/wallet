package com.example.wallet.service;

import com.example.wallet.model.TransferRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    private final RabbitTemplate rabbitTemplate;

    public WalletService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void requestTransfer(TransferRequest transferRequest) {
        rabbitTemplate.convertAndSend("transactionQueue", transferRequest);
    }
}

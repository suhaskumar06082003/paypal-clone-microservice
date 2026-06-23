package com.paypal.transaction_manager.service;

import com.paypal.transaction_manager.entity.Transaction;
import com.paypal.transaction_manager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaProducerService kafkaProducerService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, KafkaProducerService kafkaProducerService) {
        this.transactionRepository = transactionRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        Transaction saved = transactionRepository.save(transaction);
        // publish event to Kafka
        try {
            kafkaProducerService.sendTransaction(saved);
        } catch (Exception e) {
            // log and continue — do not block transaction creation on Kafka errors
        }
        return saved;
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> getTransactionsBySenderId(Long senderId) {
        return transactionRepository.findBySenderId(senderId);
    }

    @Override
    public List<Transaction> getTransactionsByReceiverId(Long receiverId) {
        return transactionRepository.findByReceiverId(receiverId);
    }
}

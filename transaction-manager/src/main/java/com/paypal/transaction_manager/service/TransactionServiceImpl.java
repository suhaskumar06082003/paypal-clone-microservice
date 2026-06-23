package com.paypal.transaction_manager.service;

import com.paypal.transaction_manager.entity.Transaction;
import com.paypal.transaction_manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
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

package com.paypal.transaction_manager.service;

import com.paypal.transaction_manager.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);
    Optional<Transaction> getTransactionById(Long id);
    List<Transaction> getAllTransactions();
    List<Transaction> getTransactionsBySenderId(Long senderId);
    List<Transaction> getTransactionsByReceiverId(Long receiverId);
}

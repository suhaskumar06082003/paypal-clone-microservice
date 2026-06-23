package com.paypal.transaction_manager.controller;

import com.paypal.transaction_manager.entity.Transaction;
import com.paypal.transaction_manager.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction saved = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<Transaction>> getBySender(@PathVariable Long senderId) {
        return ResponseEntity.ok(transactionService.getTransactionsBySenderId(senderId));
    }

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<Transaction>> getByReceiver(@PathVariable Long receiverId) {
        return ResponseEntity.ok(transactionService.getTransactionsByReceiverId(receiverId));
    }
}

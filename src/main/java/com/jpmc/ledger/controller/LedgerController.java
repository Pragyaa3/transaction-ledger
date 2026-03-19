package com.jpmc.ledger.controller;

import com.jpmc.ledger.dto.ApiResponse;
import com.jpmc.ledger.dto.CreateAccountRequest;
import com.jpmc.ledger.dto.DepositRequest;
import com.jpmc.ledger.dto.TransferRequest;
import com.jpmc.ledger.entity.Account;
import com.jpmc.ledger.entity.Transaction;
import com.jpmc.ledger.service.LedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping("/accounts")
    public ResponseEntity<ApiResponse<Account>> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        Account account = ledgerService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(account));
    }

    @PostMapping("/accounts/{id}/deposit")
    public ResponseEntity<ApiResponse<Account>> deposit(
            @PathVariable String id,
            @Valid @RequestBody DepositRequest request) {
        Account account = ledgerService.deposit(id, request);
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    @GetMapping("/accounts/{id}/balance")
    public ResponseEntity<ApiResponse<Account>> getBalance(@PathVariable String id) {
        Account account = ledgerService.getBalance(id);
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<Transaction>> transfer(
            @Valid @RequestBody TransferRequest request) {
        Transaction transaction = ledgerService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(transaction));
    }

    @GetMapping("/transactions/{accountId}")
    public ResponseEntity<ApiResponse<Page<Transaction>>> getHistory(
            @PathVariable String accountId, Pageable pageable) {
        Page<Transaction> history = ledgerService.getTransactionHistory(accountId, pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Ledger service is running"));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> root() {
        return ResponseEntity.ok(ApiResponse.success(
                "Transaction Ledger API is running. See /api/v1/health for status."));
    }
}